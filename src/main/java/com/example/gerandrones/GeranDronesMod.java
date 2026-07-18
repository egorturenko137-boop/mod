package com.example.gerandrones;

import com.example.gerandrones.client.ClientDroneControls;
import com.example.gerandrones.client.DroneRenderer;
import com.example.gerandrones.entity.GeranDroneEntity;
import com.example.gerandrones.item.DroneItem;
import com.example.gerandrones.item.RemoteControlItem;
import com.example.gerandrones.network.DroneControlPacket;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(GeranDronesMod.MOD_ID)
public class GeranDronesMod {
    public static final String MOD_ID = "gerandrones";
    private static final String PROTOCOL_VERSION = "1";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new net.minecraft.resources.ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static final RegistryObject<EntityType<GeranDroneEntity>> GERAN_2_DRONE = ENTITY_TYPES.register("geran_2_drone",
            () -> EntityType.Builder.<GeranDroneEntity>of((type, level) -> new GeranDroneEntity(type, level, GeranDroneEntity.Variant.GERAN_2), MobCategory.MISC)
                    .sized(1.2F, 0.45F).clientTrackingRange(10).updateInterval(1).build("geran_2_drone"));

    public static final RegistryObject<EntityType<GeranDroneEntity>> GERAN_5_DRONE = ENTITY_TYPES.register("geran_5_drone",
            () -> EntityType.Builder.<GeranDroneEntity>of((type, level) -> new GeranDroneEntity(type, level, GeranDroneEntity.Variant.GERAN_5), MobCategory.MISC)
                    .sized(1.7F, 0.6F).clientTrackingRange(10).updateInterval(1).build("geran_5_drone"));

    public static final RegistryObject<Item> REMOTE_CONTROL = ITEMS.register("remote_control", () -> new RemoteControlItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GERAN_2_DRONE_ITEM = ITEMS.register("geran_2_drone", () -> new DroneItem(GERAN_2_DRONE, new Item.Properties().stacksTo(8)));
    public static final RegistryObject<Item> GERAN_5_DRONE_ITEM = ITEMS.register("geran_5_drone", () -> new DroneItem(GERAN_5_DRONE, new Item.Properties().stacksTo(8)));

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("gerandrones", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.gerandrones"))
            .icon(() -> REMOTE_CONTROL.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(REMOTE_CONTROL.get());
                output.accept(GERAN_2_DRONE_ITEM.get());
                output.accept(GERAN_5_DRONE_ITEM.get());
            }).build());

    public GeranDronesMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        ENTITY_TYPES.register(bus);
        CREATIVE_TABS.register(bus);
        NETWORK.registerMessage(0, DroneControlPacket.class, DroneControlPacket::encode, DroneControlPacket::decode, DroneControlPacket::handle);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientOnly::init);
    }

    private static class ClientOnly {
        static void init() {
            EntityRenderers.register(GERAN_2_DRONE.get(), DroneRenderer::new);
            EntityRenderers.register(GERAN_5_DRONE.get(), DroneRenderer::new);
            MinecraftForge.EVENT_BUS.register(new ClientDroneControls());
        }
    }
}
