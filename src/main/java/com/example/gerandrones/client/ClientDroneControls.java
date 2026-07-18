package com.example.gerandrones.client;

import com.example.gerandrones.GeranDronesMod;
import com.example.gerandrones.entity.GeranDroneEntity;
import com.example.gerandrones.network.DroneControlPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientDroneControls {
    @SubscribeEvent public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.getCameraEntity() instanceof GeranDroneEntity drone) || mc.player == null) return;
        if (drone.hasLostControl()) {
            mc.setCameraEntity(mc.player);
            return;
        }
        float forward = (mc.options.keyUp.isDown() ? 1.0F : 0.0F) - (mc.options.keyDown.isDown() ? 1.0F : 0.0F);
        float strafe = (mc.options.keyLeft.isDown() ? 1.0F : 0.0F) - (mc.options.keyRight.isDown() ? 1.0F : 0.0F);
        GeranDronesMod.NETWORK.sendToServer(new DroneControlPacket(drone.getId(), forward, strafe, mc.options.keyJump.isDown(), mc.options.keyShift.isDown(), mc.player.getYRot(), mc.player.getXRot()));
    }

    @SubscribeEvent public void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.getCameraEntity() instanceof GeranDroneEntity && mc.options.keyInventory.matches(event.getKey(), event.getScanCode())) {
            mc.setCameraEntity(mc.player);
        }
    }
}
