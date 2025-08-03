package net.tom.dfdevkeyboardshortcuts;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static KeyBinding F_KEY;
    public static KeyBinding R_KEY;
    public static KeyBinding G_KEY;
    public static KeyBinding C_KEY;
    public static KeyBinding Q_KEY;
    public static KeyBinding Z_KEY;
    public static KeyBinding X_KEY;

    public static void register() {
        F_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dfdevkeyboardshortcuts.f_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F,
                "category.dfdevkeyboardshortcuts.keys"));

        R_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dfdevkeyboardshortcuts.r_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.dfdevkeyboardshortcuts.keys"));

        G_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dfdevkeyboardshortcuts.g_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.dfdevkeyboardshortcuts.keys"));

        C_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dfdevkeyboardshortcuts.c_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.dfdevkeyboardshortcuts.keys"));

        Q_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dfdevkeyboardshortcuts.q_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Q,
                "category.dfdevkeyboardshortcuts.keys"));

        Z_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dfdevkeyboardshortcuts.z_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.dfdevkeyboardshortcuts.keys"));

        X_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dfdevkeyboardshortcuts.x_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "category.dfdevkeyboardshortcuts.keys"));
    }
}
