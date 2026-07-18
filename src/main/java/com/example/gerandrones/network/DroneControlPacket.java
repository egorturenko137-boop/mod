package com.example.gerandrones.network;

import com.example.gerandrones.entity.GeranDroneEntity;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record DroneControlPacket(int entityId, float forward, float strafe, boolean up, boolean down, float yaw, float pitch) {
    public static void encode(DroneControlPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeFloat(packet.forward);
        buf.writeFloat(packet.strafe);
        buf.writeBoolean(packet.up);
        buf.writeBoolean(packet.down);
        buf.writeFloat(packet.yaw);
        buf.writeFloat(packet.pitch);
    }

    public static DroneControlPacket decode(FriendlyByteBuf buf) {
        return new DroneControlPacket(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(DroneControlPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level().getEntity(packet.entityId) instanceof GeranDroneEntity drone) {
                drone.acceptInput(player, packet.forward, packet.strafe, packet.up, packet.down, packet.yaw, packet.pitch);
                if (drone.hasLostControl()) player.setCamera(player);
            }
        });
        context.get().setPacketHandled(true);
    }
}
