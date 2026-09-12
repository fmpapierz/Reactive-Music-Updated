package circuitlord.reactivemusic.neoforge;

import circuitlord.reactivemusic.ReactiveMusic;
import circuitlord.reactivemusic.SongPicker;
import circuitlord.reactivemusic.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

import static net.minecraft.commands.Commands.literal;

@Mod("reactivemusic")
public class ReactiveMusicNeoForge {

    public ReactiveMusicNeoForge(IEventBus modEventBus) {
        ReactiveMusic.init();
        modEventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (modContainer, parent) -> ModConfig.createScreen(parent)
        );
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ReactiveMusic::initClient);
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(literal("reactivemusic")
                .executes(context -> {
                    Minecraft mc = Minecraft.getInstance();
                    Screen screen = ModConfig.createScreen(mc.gui.screen());
                    mc.execute(() -> mc.gui.setScreen(screen));
                    return 1;
                })

                .then(literal("logBlockCounter")
                        .executes(context -> {
                            SongPicker.queuedToPrintBlockCounter = true;
                            return 1;
                        })
                )

                .then(literal("toggleSoundEventLogging")
                        .executes(context -> {
                            ReactiveMusic.printSoundEvents = !ReactiveMusic.printSoundEvents;
                            return 1;
                        })
                )

                .then(literal("blacklistDimension")
                        .executes(context -> {
                            Minecraft mc = Minecraft.getInstance();
                            String key = mc.level.dimension().identifier().toString();

                            if (ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: " + key + " was already in blacklist."));
                                return 1;
                            }

                            mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: Added " + key + " to blacklist."));
                            ReactiveMusic.config.blacklistedDimensions.add(key);
                            ModConfig.saveConfig();
                            return 1;
                        })
                )

                .then(literal("unblacklistDimension")
                        .executes(context -> {
                            Minecraft mc = Minecraft.getInstance();
                            String key = mc.level.dimension().identifier().toString();

                            if (!ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: " + key + " was not in blacklist."));
                                return 1;
                            }

                            mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: Removed " + key + " from blacklist."));
                            ReactiveMusic.config.blacklistedDimensions.remove(key);
                            ModConfig.saveConfig();
                            return 1;
                        })
                )

                .then(literal("toggleLogging")
                        .executes(context -> {
                            ReactiveMusic.chatLoggingEnabled = !ReactiveMusic.chatLoggingEnabled;
                            Minecraft.getInstance().player.sendSystemMessage(
                                    Component.literal("[ReactiveMusic]: Logging enabled: " + ReactiveMusic.chatLoggingEnabled));
                            return 1;
                        })
                )
        );
    }
}
