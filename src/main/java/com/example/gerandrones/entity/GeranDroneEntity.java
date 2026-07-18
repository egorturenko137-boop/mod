package com.example.gerandrones.entity;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GeranDroneEntity extends Entity {
    public enum Variant { GERAN_2(0.45D, 0.045D), GERAN_5(0.62D, 0.055D); final double speed; final double lift; Variant(double speed, double lift) { this.speed = speed; this.lift = lift; } }

    private static final EntityDataAccessor<Boolean> CONTROL_LOST = SynchedEntityData.defineId(GeranDroneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DAMAGE_TAKEN = SynchedEntityData.defineId(GeranDroneEntity.class, EntityDataSerializers.FLOAT);
    private final Variant variant;
    private UUID controller;
    private float pendingForward;
    private float pendingStrafe;
    private boolean pendingUp;
    private boolean pendingDown;

    public GeranDroneEntity(EntityType<?> type, Level level) { this(type, level, Variant.GERAN_2); }

    public GeranDroneEntity(EntityType<?> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
        noPhysics = false;
    }

    @Override protected void defineSynchedData() {
        entityData.define(CONTROL_LOST, false);
        entityData.define(DAMAGE_TAKEN, 0.0F);
    }

    public void setController(Player player) { controller = player.getUUID(); }
    public boolean isControlledBy(Player player) { return controller != null && controller.equals(player.getUUID()) && !hasLostControl(); }
    public boolean hasLostControl() { return entityData.get(CONTROL_LOST); }
    public float getDamageTaken() { return entityData.get(DAMAGE_TAKEN); }

    public void acceptInput(ServerPlayer player, float forward, float strafe, boolean up, boolean down, float yaw, float pitch) {
        if (!isControlledBy(player)) return;
        setYRot(yaw);
        setXRot(pitch);
        pendingForward = forward;
        pendingStrafe = strafe;
        pendingUp = up;
        pendingDown = down;
    }

    @Override public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (hasLostControl()) {
                setDeltaMovement(getDeltaMovement().add(0.0D, -0.06D, 0.0D).scale(0.98D));
            } else {
                Vec3 look = Vec3.directionFromRotation(getXRot(), getYRot());
                Vec3 right = Vec3.directionFromRotation(0.0F, getYRot() + 90.0F);
                Vec3 motion = look.scale(pendingForward * variant.speed).add(right.scale(pendingStrafe * variant.speed));
                double vertical = (pendingUp ? variant.lift : 0.0D) - (pendingDown ? variant.lift : 0.0D);
                setDeltaMovement(motion.x, motion.y + vertical, motion.z);
            }
        }
        move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.91D));
    }

    @Override public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) return false;
        float total = getDamageTaken() + amount;
        entityData.set(DAMAGE_TAKEN, total);
        if (total >= 5.0F) entityData.set(CONTROL_LOST, true);
        return true;
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(CONTROL_LOST, tag.getBoolean("ControlLost"));
        entityData.set(DAMAGE_TAKEN, tag.getFloat("DamageTaken"));
        if (tag.hasUUID("Controller")) controller = tag.getUUID("Controller");
    }

    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("ControlLost", hasLostControl());
        tag.putFloat("DamageTaken", getDamageTaken());
        Optional.ofNullable(controller).ifPresent(uuid -> tag.putUUID("Controller", uuid));
    }
}
