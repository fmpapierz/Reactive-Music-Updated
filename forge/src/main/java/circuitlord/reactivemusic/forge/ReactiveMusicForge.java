package circuitlord.reactivemusic.forge;

import circuitlord.reactivemusic.ReactiveMusic;
import circuitlord.reactivemusic.SongPicker;
import circuitlord.reactivemusic.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.minecraft.commands.Commands.literal;

@Mod("reactivemusic")
public class ReactiveMusicForge {

    public ReactiveMusicForge(FMLJavaModLoadingContext context) {
        ReactiveMusic.init();

        FMLClientSetupEvent.getBus(context.getModBusGroup()).addListener(this::onClientSetup);
        RegisterClientCommandsEvent.BUS.addListener(this::onRegisterClientCommands);

        MinecraftForge.registerConfigScreen(ModConfig::createScreen);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ReactiveMusic::initClient);
    }

    private void onRegisterClientCommands(RegisterClientCommandsEvent event) {
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
