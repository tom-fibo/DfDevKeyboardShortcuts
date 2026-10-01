package net.tom.dfdevkeyboardshortcuts.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.tom.dfdevkeyboardshortcuts.DfDevKeyboardShortcuts;
import net.tom.dfdevkeyboardshortcuts.DfDevKeyboardShortcutsClient;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {

    private static final int scancodeZ = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Z);
    private static final int scancodeX = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_X);
    private static final int scancodeC = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_C);
    private static final int scancodeF = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_F);
    private static final int scancodeG = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_G);
    private static final int scancodeQ = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q);
    private static final int scancodeR = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_R);

    @Inject(method = "keyPress", at = @At(value = "HEAD"), cancellable = true)
    private void onKey(long window, int key, KeyEvent input, CallbackInfo ci) {
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
        Minecraft client = Minecraft.getInstance();
        //KeyBinding dropKey = client.options.dropKey;
        //KeyBinding loadToolbarKey = client.options.loadToolbarActivatorKey;
        if (client.gui.screen() == null
                && (scancode == scancodeQ || scancode == scancodeZ)
                && client.player != null) {
            ItemStack mainHandStack = client.player.getMainHandItem();
            if (mainHandStack.getItem() != Items.CONCRETE.lime()
                    || mainHandStack.get(DataComponents.CUSTOM_DATA) == null) {
                if (scancode == scancodeQ) {
                    boolean entireStack = (modifiers & GLFW.GLFW_MOD_CONTROL) > 0;
                    client.player.drop(entireStack);
                } else if (scancode == scancodeZ) {
                    DfDevKeyboardShortcutsClient.updateInventory = true;
                }
                ci.cancel();
                return;
            }
        }

        if (client.hitResult == null || client.hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult hitResult = (BlockHitResult) client.hitResult;
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
