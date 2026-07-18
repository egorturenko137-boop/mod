package com.example.gerandrones.item;

import com.example.gerandrones.entity.GeranDroneEntity;
import java.util.Comparator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RemoteControlItem extends Item {
    public RemoteControlItem(Properties properties) { super(properties); }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        level.getEntitiesOfClass(GeranDroneEntity.class, player.getBoundingBox().inflate(64.0D), drone -> !drone.hasLostControl())
                .stream().min(Comparator.comparingDouble(drone -> drone.distanceToSqr(player))).ifPresent(drone -> {
                    drone.setController(player);
                    ((ServerPlayer) player).setCamera(drone);
                });
        return InteractionResultHolder.consume(stack);
    }

    public static void returnCamera(ServerPlayer player) { player.setCamera(player); }
}
