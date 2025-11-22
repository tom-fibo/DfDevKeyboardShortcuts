package net.tom.dfdevkeyboardshortcuts.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.tom.dfdevkeyboardshortcuts.DfDevKeyboardShortcutsClient;
import net.tom.dfdevkeyboardshortcuts.KeyBinds;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        KeyBinding dropKey = client.options.dropKey;
        KeyBinding loadToolbarKey = client.options.loadToolbarActivatorKey;
        if (client.currentScreen == null
                && (dropKey.matchesKey(key, scancode) || loadToolbarKey.matchesKey(key, scancode)
                        || KeyBinds.Z_KEY.matchesKey(key, scancode))
                && client.player != null) {
            ItemStack mainHandStack = client.player.getMainHandStack();
            if (mainHandStack.getItem() != Items.NETHERITE_BLOCK
                    || mainHandStack.get(DataComponentTypes.CUSTOM_DATA) == null) {
                if (dropKey.matchesKey(key, scancode)) {
                    boolean entireStack = MinecraftClient.IS_SYSTEM_MAC ? modifiers == GLFW.GLFW_MOD_SUPER
                            : modifiers == GLFW.GLFW_MOD_CONTROL;
                    client.player.dropSelectedItem(entireStack);
                } else if (loadToolbarKey.matchesKey(key, scancode)) {
                    net.minecraft.client.option.KeyBinding.onKeyPressed(loadToolbarKey.getDefaultKey());
                } else if (KeyBinds.Z_KEY.matchesKey(key, scancode)) {
                    DfDevKeyboardShortcutsClient.updateInventory = true;
                }
                ci.cancel();
                return;
            }
        }

        if (client.crosshairTarget == null || client.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult hitResult = (BlockHitResult) client.crosshairTarget;
        if (!DfDevKeyboardShortcutsClient.isActive(hitResult.getBlockPos())) {
            return;
        }

        boolean handled = false;
        if (KeyBinds.F_KEY.matchesKey(key, scancode)) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.NETHERRACK,
                    Items.RED_NETHER_BRICKS, hitResult);
            handled = true;
        } else if (KeyBinds.R_KEY.matchesKey(key, scancode)) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.COAL_BLOCK,
                    Items.END_STONE,
                    hitResult);
            handled = true;
        } else if (KeyBinds.G_KEY.matchesKey(key, scancode)) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.COBBLESTONE,
                    Items.OAK_PLANKS,
                    hitResult);
            handled = true;
        } else if (KeyBinds.C_KEY.matchesKey(key, scancode)) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player,
                    Items.MOSSY_COBBLESTONE, Items.BRICKS,
                    hitResult);
            handled = true;
        } else if (KeyBinds.Q_KEY.matchesKey(key, scancode)) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.LAPIS_ORE,
                    Items.LAPIS_BLOCK,
                    hitResult);
            handled = true;
        } else if (KeyBinds.Z_KEY.matchesKey(key, scancode)) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.DIAMOND_BLOCK,
                    Items.GOLD_BLOCK,
                    hitResult);
            handled = true;
        } else if (KeyBinds.X_KEY.matchesKey(key, scancode)) {
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
