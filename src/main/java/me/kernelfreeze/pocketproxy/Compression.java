/*
    Copyright (c) 2016 Voxelwind contributors
    Copyright (c) 2017 KernelFreeze

    The MIT License

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software
    and associated documentation files (the "Software"), to deal in the Software without restriction,
    including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
    and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
    subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions
    of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
    TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
    THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
    CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
    IN THE SOFTWARE.
 */

package me.kernelfreeze.pocketproxy;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.jni.zlib.JavaZlib;
import net.md_5.bungee.jni.zlib.NativeZlib;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

@SuppressWarnings("unchecked")
public class Compression {
    private static final ThreadLocal<BungeeZlib> inflaterLocal = ThreadLocal.withInitial(() -> {
        BungeeZlib zlib = (BungeeZlib) new NativeCode("native-compress", JavaZlib.class, NativeZlib.class).newInstance();
        zlib.init(false, Deflater.DEFAULT_COMPRESSION);
        return zlib;
    });

    private static final ThreadLocal<BungeeZlib> deflaterLocal = ThreadLocal.withInitial(() -> {
        BungeeZlib zlib = (BungeeZlib) new NativeCode("native-compress", JavaZlib.class, NativeZlib.class).newInstance();
        zlib.init(true, Deflater.DEFAULT_COMPRESSION);
        return zlib;
    });

    /**
     * Decompresses a buffer.
     *
     * @param buffer the buffer to decompress
     * @return the decompressed buffer
     * @throws DataFormatException if data could not be inflated
     */
    public static ByteBuf inflate(ByteBuf buffer) throws DataFormatException {
        // Ensure that this buffer is direct.
        ByteBuf source = null;
        ByteBuf decompressed = PooledByteBufAllocator.DEFAULT.directBuffer();

        try {
            if (!buffer.isDirect()) {
                // We don't have a direct buffer. Create one.
                ByteBuf temporary = PooledByteBufAllocator.DEFAULT.directBuffer();
                temporary.writeBytes(buffer);
                source = temporary;
            } else {
                source = buffer;
            }

            inflaterLocal.get().process(source, decompressed);
            decompressed.resetReaderIndex();
            return decompressed;
        } catch (DataFormatException e) {
            decompressed.release();
            throw e;
        } finally {
            if (source != null && source != buffer) {
                source.release();
            }
        }
    }

    /**
     * Compresses a buffer.
     *
     * @param buffer the buffer to compress
     * @return a new compressed buffer
     * @throws DataFormatException if data could not be deflated
     */
    public static ByteBuf deflate(ByteBuf buffer) throws DataFormatException {
        ByteBuf dest = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            deflate(buffer, dest);
        } catch (DataFormatException e) {
            dest.release();
            throw e;
        }
        return dest;
    }

    /**
     * Compresses a {@link ByteBuf}.
     *
     * @param toCompress the buffer to compress
     * @param into       the buffer to compress into
     * @throws DataFormatException if data could not be deflated
     */
    public static void deflate(ByteBuf toCompress, ByteBuf into) throws DataFormatException {
        ByteBuf destination = null;
        ByteBuf source = null;

        try {
            if (!toCompress.isDirect()) {
                // Source is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                source = PooledByteBufAllocator.DEFAULT.directBuffer();
                source.writeBytes(toCompress);
            } else {
                source = toCompress;
            }

            if (!into.isDirect()) {
                // Destination is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                destination = PooledByteBufAllocator.DEFAULT.directBuffer();
            } else {
                destination = into;
            }

            deflaterLocal.get().process(source, destination);

            if (destination != into) {
                into.writeBytes(destination);
            }
        } finally {
            if (source != null && source != toCompress) {
                source.release();
            }
            if (destination != null && destination != into) {
                destination.release();
                destination.resetReaderIndex();
            }
        }
    }
}
