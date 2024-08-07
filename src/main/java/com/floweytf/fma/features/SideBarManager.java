package com.floweytf.fma.features;

import com.floweytf.fma.FMAConfig;
import static com.floweytf.fma.util.FormatUtil.join;
import static com.floweytf.fma.util.FormatUtil.withColor;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;

public class SideBarManager {
    private final Component title;
    private List<Component> builtinText = List.of();
    private List<Component> additionalText = List.of();
    private final int textColor;
    private final int altColor;
    private final int errorColor;

    public SideBarManager(FMAConfig config) {
        title = join(
            Component.literal("     "),
            withColor("[", config.appearance.bracketColor),
            withColor(config.appearance.tagText, config.appearance.tagColor).withStyle(ChatFormatting.BOLD),
            withColor("] ", config.appearance.bracketColor),
            Component.literal("     ")
        );
        textColor = config.appearance.textColor;
        altColor = config.appearance.altTextColor;
        errorColor = config.appearance.errorColor;

        // Simulate a tick to avoid bug
        onTick(Minecraft.getInstance());
    }

    public void onTick(Minecraft mc) {
        final var raw = Optional.ofNullable(mc.gui.getTabList().header).map(Component::getString).orElse("");
        final var start = raw.indexOf("<");
        final var end = raw.indexOf(">");

        builtinText = new ArrayList<>();
        if (start == -1 || end == -1) {
            builtinText.add(Component.translatable("hud.fma.sidebar.shard", withColor("unknown (bug)", errorColor)));
        } else {
            final var shard = raw.substring(start + 1, end);
            builtinText.add(Component.translatable("hud.fma.sidebar.shard", withColor(shard, altColor)));
        }

        final var conn = Minecraft.getInstance().getConnection();

        if (conn != null && conn.getServerData() != null) {
            builtinText.add(Component.translatable("hud.fma.sidebar.ip", withColor(conn.getServerData().ip, altColor)));
        }
    }

    public void setAdditionalText(List<Component> additionalText) {
        this.additionalText = additionalText;
    }

    public void render(Minecraft mc, PoseStack stack) {
        final var font = mc.fontFilterFishy;
        final var lines = Stream.concat(
            builtinText.stream(),
            additionalText.stream()
        ).toList();

        final var width = Math.max(font.width(title), lines.stream().mapToInt(font::width).max().orElse(0));
        // okay render everything, very stupid blah blah blah
        final int height = font.lineHeight * (lines.size() + 1);

        final int sw = mc.getWindow().getGuiScaledWidth();
        final int sh = mc.getWindow().getGuiScaledHeight();
        final int startX = sw - width - 2;
        final int startY = (sh - height) / 2;

        Gui.fill(stack, startX - 2, startY - 2, sw, startY + height + 2, mc.options.getBackgroundColor(0.3F));

        Gui.drawCenteredString(stack, font, title, startX + width / 2, startY, textColor);
        int textY = startY + font.lineHeight;

        for (final Component line : lines) {
            Gui.drawString(stack, font, line, startX, textY, textColor);
            textY += font.lineHeight;
        }
    }
}
