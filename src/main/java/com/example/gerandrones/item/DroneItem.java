package com.example.gerandrones.item;

import com.example.gerandrones.entity.GeranDroneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class DroneItem extends Item {
    private final RegistryObject<EntityType<GeranDroneEntity>> droneType;

    public DroneItem(RegistryObject<EntityType<GeranDroneEntity>> droneType, Properties properties) {
        super(properties);
        this.droneType = droneType;
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.SUCCESS;
        Vec3 pos = context.getClickLocation().add(0.0D, 0.35D, 0.0D);
        GeranDroneEntity drone = droneType.get().create(level);
        if (drone == null) return InteractionResult.FAIL;
        drone.moveTo(pos.x, pos.y, pos.z, context.getRotation(), 0.0F);
        level.addFreshEntity(drone);
        if (!context.getPlayer().getAbilities().instabuild) context.getItemInHand().shrink(1);
        return InteractionResult.CONSUME;
    }
}
