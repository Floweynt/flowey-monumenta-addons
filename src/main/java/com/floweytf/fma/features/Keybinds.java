package com.floweytf.fma.features;

import com.floweytf.fma.chat.ChatChannelManager;
import com.floweytf.fma.util.ChatUtil;
import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.api.PriorityKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    private static abstract class PriorityKeybind extends AmecsKeyBinding implements PriorityKeyBinding {
        public PriorityKeybind(String name, InputConstants.Type type, int code, String category, KeyModifiers mods) {
            super(name, type, code, category, mods);
            KeyBindingHelper.registerKeyBinding(this);
        }
    }

    private static class ChatChannelControlKeybind extends PriorityKeybind {
        private final Runnable action;

        public ChatChannelControlKeybind(String name, InputConstants.Type type, int code, String category,
                                         KeyModifiers mods, Runnable action) {
            super(name, type, code, category, mods);
            this.action = action;
        }

        @Override
        public boolean onPressedPriority() {
            final var screen = Minecraft.getInstance().screen;
            if (screen instanceof ChatScreen) {
                action.run();
                return true;
            }

            return false;
        }
    }

    public static void init() {
        new ChatChannelControlKeybind(
            "key.fma.cycle_chat_builtin",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            "category.fma",
            new KeyModifiers(false, true, false),
            () -> {
                final var manager = ChatChannelManager.getInstance();
                if (manager.isDm()) {
                    manager.toggleDmBuiltin();
                } else {
                    manager.cycleBuiltins();
                }
            }
        );

        new ChatChannelControlKeybind(
            "key.fma.cycle_chat_dm",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            "category.fma",
            new KeyModifiers(false, true, true),
            () -> {
                final var manager = ChatChannelManager.getInstance();
                if (!manager.isDm()) {
                    manager.toggleDmBuiltin();
                } else {
                    manager.cycleDm();
                }
            }
        );

        new ChatChannelControlKeybind(
            "key.fma.toggle_dm_builtin",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.fma",
            new KeyModifiers(false, false, false),
            () -> ChatChannelManager.getInstance().cycleDm()
        );

        ChatChannelManager.BUILTIN_CHANNELS.forEach(systemChatChannel -> {
            // unbound by default
            new ChatChannelControlKeybind(
                "key.fma.set_channel_" + systemChatChannel.command(),
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.fma",
                new KeyModifiers(false, false, false),
                () -> ChatChannelManager.getInstance().setChannel(systemChatChannel)
            );
        });

        KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(
            "key.fma.meow",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.fma",
            new KeyModifiers(false, false, false)
        ) {
            @Override
            public void onPressed() {
                ChatUtil.sendCommand("g meow");
            }
        });
    }
}
