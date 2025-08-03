package net.tom.dfdevkeyboardshortcuts.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.tom.dfdevkeyboardshortcuts.DfDevKeyboardShortcutsClient;
import net.tom.dfdevkeyboardshortcuts.KeyBinds;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) client.crosshairTarget;
            if (!DfDevKeyboardShortcutsClient.isActive(hitResult.getBlockPos())) {
                return;
            }
            boolean handled = false;
            if (key == KeyBinds.F_KEY.getDefaultKey().getCode()) {
                DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.NETHERRACK,
                        Items.RED_NETHER_BRICKS, hitResult);
                handled = true;
            } else if (key == KeyBinds.R_KEY.getDefaultKey().getCode()) {
                DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.COAL_BLOCK,
                        Items.END_STONE,
                        hitResult);
                handled = true;
            } else if (key == KeyBinds.G_KEY.getDefaultKey().getCode()) {
                DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.COBBLESTONE,
                        Items.OAK_PLANKS,
                        hitResult);
                handled = true;
            } else if (key == KeyBinds.C_KEY.getDefaultKey().getCode()) {
                DfDevKeyboardShortcutsClient.placeBlock(client.player,
                        Items.MOSSY_COBBLESTONE, Items.BRICKS,
                        hitResult);
                handled = true;
            } else if (key == KeyBinds.Q_KEY.getDefaultKey().getCode()) {
                DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.LAPIS_ORE,
                        Items.LAPIS_BLOCK,
                        hitResult);
                handled = true;
            } else if (key == KeyBinds.Z_KEY.getDefaultKey().getCode()) {
                DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.DIAMOND_BLOCK,
                        Items.GOLD_BLOCK,
                        hitResult);
                handled = true;
            } else if (key == KeyBinds.X_KEY.getDefaultKey().getCode()) {
                DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.EMERALD_ORE,
                        Items.EMERALD_BLOCK,
                        hitResult);
                handled = true;
            }

            if (handled) {
                ci.cancel();
            }
        }
    }
}
