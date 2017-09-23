package me.kernelfreeze.bedrockproxy;

import lombok.Getter;
import me.kernelfreeze.bedrockproxy.packets.*;
import me.kernelfreeze.bedrockproxy.raknet.RakNetPacket;

/**
 * @author KernelFreeze
 * @since 6/06/17
 */
public class PacketRegistry {
    @SuppressWarnings("unchecked")
    public static void handlePacket(RakNetPacket packet, BedrockPlayer player) {
        try {
            NetworkType type = NetworkType.getById(packet.getId());

            if (type != NetworkType.UNKOWN && type.getHandle() != null) {
                DataPacket inst = (DataPacket) type.getHandle().getConstructor(RakNetPacket.class).newInstance(packet);
                inst.setPlayer(player);
                inst.handle();
            } else {
                BedrockProxy.getInstance().getLogger().warning(
                        String.format("Unknown packet 0x%s from %s",
                                Integer.toHexString(packet.getId()).toUpperCase(),
                                player.getSession().getAddress())
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.disconnect(e.getMessage());
        }
    }

    public enum NetworkType {
        UNKOWN(-1),
        LOGIN_PACKET(0x00, LoginPacket.class),
        PLAY_STATUS_PACKET(0x02, PlayStatusPacket.class),
        SERVER_TO_CLIENT_HANDSHAKE_PACKET(0x03, ServerHandshakePacket.class),
        CLIENT_TO_SERVER_HANDSHAKE_PACKET(0x04),
        DISCONNECT_PACKET(0x05, DisconnectPacket.class),
        RESOURCE_PACKS_INFO_PACKET(0x06),
        RESOURCE_PACK_STACK_PACKET(0x07),
        RESOURCE_PACK_CLIENT_RESPONSE_PACKET(0x08),
        TEXT_PACKET(0x09),
        SET_TIME_PACKET(0x0a),
        START_GAME_PACKET(0x0b),
        ADD_PLAYER_PACKET(0x0c),
        ADD_ENTITY_PACKET(0x0d),
        REMOVE_ENTITY_PACKET(0x0e),
        ADD_ITEM_ENTITY_PACKET(0x0f),
        ADD_HANGING_ENTITY_PACKET(0x10),
        TAKE_ITEM_ENTITY_PACKET(0x11),
        MOVE_ENTITY_PACKET(0x12),
        MOVE_PLAYER_PACKET(0x13),
        RIDER_JUMP_PACKET(0x14),
        REMOVE_BLOCK_PACKET(0x15),
        UPDATE_BLOCK_PACKET(0x16),
        ADD_PAINTING_PACKET(0x17),
        EXPLODE_PACKET(0x18),
        LEVEL_SOUND_EVENT_PACKET(0x19),
        LEVEL_EVENT_PACKET(0x1a),
        BLOCK_EVENT_PACKET(0x1b),
        ENTITY_EVENT_PACKET(0x1c),
        MOB_EFFECT_PACKET(0x1d),
        UPDATE_ATTRIBUTES_PACKET(0x1e),
        MOB_EQUIPMENT_PACKET(0x1f),
        MOB_ARMOR_EQUIPMENT_PACKET(0x20),
        INTERACT_PACKET(0x21),
        BLOCK_PICK_REQUEST_PACKET(0x22),
        USE_ITEM_PACKET(0x23),
        PLAYER_ACTION_PACKET(0x24),
        ENTITY_FALL_PACKET(0x25),
        HURT_ARMOR_PACKET(0x26),
        SET_ENTITY_DATA_PACKET(0x27),
        SET_ENTITY_MOTION_PACKET(0x28),
        SET_ENTITY_LINK_PACKET(0x29),
        SET_HEALTH_PACKET(0x2a),
        SET_SPAWN_POSITION_PACKET(0x2b),
        ANIMATE_PACKET(0x2c),
        RESPAWN_PACKET(0x2d),
        DROP_ITEM_PACKET(0x2e),
        INVENTORY_ACTION_PACKET(0x2f),
        CONTAINER_OPEN_PACKET(0x30),
        CONTAINER_CLOSE_PACKET(0x31),
        CONTAINER_SET_SLOT_PACKET(0x32),
        CONTAINER_SET_DATA_PACKET(0x33),
        CONTAINER_SET_CONTENT_PACKET(0x34),
        CRAFTING_DATA_PACKET(0x35),
        CRAFTING_EVENT_PACKET(0x36),
        ADVENTURE_SETTINGS_PACKET(0x37),
        BLOCK_ENTITY_DATA_PACKET(0x38),
        PLAYER_INPUT_PACKET(0x39),
        FULL_CHUNK_DATA_PACKET(0x3a),
        SET_COMMANDS_ENABLED_PACKET(0x3b),
        SET_DIFFICULTY_PACKET(0x3c),
        CHANGE_DIMENSION_PACKET(0x3d),
        SET_PLAYER_GAME_TYPE_PACKET(0x3e),
        PLAYER_LIST_PACKET(0x3f),
        SIMPLE_EVENT_PACKET(0x40),
        EVENT_PACKET(0x41),
        SPAWN_EXPERIENCE_ORB_PACKET(0x42),
        CLIENTBOUND_MAP_ITEM_DATA_PACKET(0x43),
        MAP_INFO_REQUEST_PACKET(0x44),
        REQUEST_CHUNK_RADIUS_PACKET(0x45),
        CHUNK_RADIUS_UPDATED_PACKET(0x46),
        ITEM_FRAME_DROP_ITEM_PACKET(0x47),
        REPLACE_ITEM_IN_SLOT_PACKET(0x48),
        GAME_RULES_CHANGED_PACKET(0x49),
        CAMERA_PACKET(0x4a),
        ADD_ITEM_PACKET(0x4b),
        BOSS_EVENT_PACKET(0x4c),
        SHOW_CREDITS_PACKET(0x4d),
        AVAILABLE_COMMANDS_PACKET(0x4e),
        COMMAND_STEP_PACKET(0x4f),
        COMMAND_BLOCK_UPDATE_PACKET(0x50),
        UPDATE_TRADE_PACKET(0x51),
        UPDATE_EQUIP_PACKET(0x52),
        RESOURCE_PACK_DATA_INFO_PACKET(0x53),
        RESOURCE_PACK_CHUNK_DATA_PACKET(0x54),
        RESOURCE_PACK_CHUNK_REQUEST_PACKET(0x55),
        TRANSFER_PACKET(0x56),
        PLAY_SOUND_PACKET(0x57),
        STOP_SOUND_PACKET(0x58),
        SET_TITLE_PACKET(0x59),
        ADD_BEHAVIOR_TREE_PACKET(0x5a),
        STRUCTURE_BLOCK_UPDATE_PACKET(0x5b),
        SHOW_STORE_OFFER_PACKET(0x5c),
        PURCHASE_RECEIPT_PACKET(0x5d),
        BATCH_PACKET(0xFE, BatchPacket.class);

        @Getter
        private final short id;

        @Getter
        private Class handle;

        NetworkType(int id) {
            this.id = (short) id;
        }

        NetworkType(int id, Class handle) {
            this.id = (short) id;
            this.handle = handle;
        }

        public static NetworkType getById(short id) {
            for (NetworkType e : values()) {
                if (e.id == id) {
                    return e;
                }
            }
            return UNKOWN;
        }
    }
}
