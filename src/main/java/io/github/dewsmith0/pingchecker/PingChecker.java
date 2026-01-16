package io.github.dewsmith0.pingchecker;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Environment(EnvType.CLIENT)
public class PingChecker implements ClientModInitializer {
	public static final String MOD_ID = "pingchecker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient MC = MinecraftClient.getInstance();
	@Override
	public void onInitializeClient() {
		LOGGER.info("PingChecker coming right up!");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                dispatcher.register(ClientCommandManager.literal("checkping")
                        .then(ClientCommandManager.argument("target", StringArgumentType.string())
                                .suggests(new PlayerSuggestionProvider())
                                .executes(PingChecker::checkPing)));
        });
	}

    private static int checkPing(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        PlayerListEntry entry;
        String targetName = StringArgumentType.getString(context, "target");
        ClientPlayNetworkHandler handler = MC.getNetworkHandler();
        if (handler == null) {
            player.sendMessage(Text.translatable("pingchecker.error.no_handler").formatted(Formatting.RED), false);
            return 2;
        }
        entry = handler.getPlayerListEntry(targetName);
        if (entry == null) {
            player.sendMessage(Text.translatable("pingchecker.error.not_found").formatted(Formatting.RED), false);
            return 3;
        }
        player.sendMessage(Text.translatable("pingchecker.success", targetName, entry.getLatency()).formatted(Formatting.AQUA), false);


         return 0;
    }
}