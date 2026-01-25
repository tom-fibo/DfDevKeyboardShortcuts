package net.tom.dfdevkeyboardshortcuts.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.tom.dfdevkeyboardshortcuts.DfDevKeyboardShortcuts;
import net.tom.dfdevkeyboardshortcuts.DfDevKeyboardShortcutsClient;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    private static final int scancodeZ = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Z);
    private static final int scancodeX = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_X);
    private static final int scancodeC = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_C);
    private static final int scancodeF = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_F);
    private static final int scancodeG = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_G);
    private static final int scancodeQ = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q);
    private static final int scancodeR = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_R);

    @Inject(method = "onKey", at = @At(value = "HEAD"), cancellable = true)
    private void onKey(long window, int key, KeyInput input, CallbackInfo ci) {
        // Extract the data from the new object so the rest of your code works
        int scancode = input.scancode();
        int modifiers = input.modifiers();
        //key: 0:off, 1:on, 2:held
        //scancode: key #
        //modifers: shift:1, control:2, alt:4
        DfDevKeyboardShortcuts.LOGGER.info("onKey - key:{}, scancode:{}, modifiers:{}", key, scancode, modifiers);
        if (key != 1) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        //KeyBinding dropKey = client.options.dropKey;
        //KeyBinding loadToolbarKey = client.options.loadToolbarActivatorKey;
        if (client.currentScreen == null
                && (scancode == scancodeQ || scancode == scancodeZ)
                && client.player != null) {
            ItemStack mainHandStack = client.player.getMainHandStack();
            if (mainHandStack.getItem() != Items.LIME_CONCRETE
                    || mainHandStack.get(DataComponentTypes.CUSTOM_DATA) == null) {
                if (scancode == scancodeQ) {
                    boolean entireStack = (modifiers & GLFW.GLFW_MOD_CONTROL) > 0;
                    client.player.dropSelectedItem(entireStack);
                } else if (scancode == scancodeZ) {
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
        if (scancode == scancodeF) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.NETHERRACK,
                    Items.RED_NETHER_BRICKS, hitResult);
            handled = true;
        } else if (scancode == scancodeR) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.COAL_BLOCK,
                    Items.END_STONE,
                    hitResult);
            handled = true;
        } else if (scancode == scancodeG) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.COBBLESTONE,
                    Items.OAK_PLANKS,
                    hitResult);
            handled = true;
        } else if (scancode == scancodeC) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player,
                    Items.MOSSY_COBBLESTONE, Items.BRICKS,
                    hitResult);
            handled = true;
        } else if (scancode == scancodeQ) {
            DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.LAPIS_ORE,
                    Items.LAPIS_BLOCK,
                    hitResult);
            handled = true;
        } else if (scancode == scancodeZ) {
            if ((modifiers & 2) > 0) {
                DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.NETHERITE_BLOCK,
                        Items.NETHERITE_BLOCK,
                        hitResult);
                handled = true;
            } else {
                DfDevKeyboardShortcutsClient.placeBlock(client.player, Items.DIAMOND_BLOCK,
                        Items.GOLD_BLOCK,
                        hitResult);
                handled = true;
            }
        } else if (scancode == scancodeX) {
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
