package com.example.gerandrones.client;

import com.example.gerandrones.GeranDronesMod;
import com.example.gerandrones.entity.GeranDroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class DroneRenderer extends EntityRenderer<GeranDroneEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GeranDronesMod.MOD_ID, "textures/entity/drone.png");

    public DroneRenderer(EntityRendererProvider.Context context) { super(context); }

    @Override public void render(GeranDroneEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(1.5F, 0.35F, 0.8F);
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.IRON_BLOCK.defaultBlockState(), poseStack, buffer, packedLight, 0);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override public ResourceLocation getTextureLocation(GeranDroneEntity entity) { return TEXTURE; }
}
