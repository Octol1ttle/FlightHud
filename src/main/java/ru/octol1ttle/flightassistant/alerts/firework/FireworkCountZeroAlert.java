package ru.octol1ttle.flightassistant.alerts.firework;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.ECAMSoundData;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class FireworkCountZeroAlert extends AbstractAlert {

    private final FireworkController firework;

    public FireworkCountZeroAlert(FireworkController firework) {
        this.firework = firework;
    }

    @Override
    public boolean isTriggered() {
        return firework.safeFireworkCount <= 0;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return ECAMSoundData.MASTER_CAUTION;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedFont(textRenderer, context, Text.translatable("alerts.flightassistant.firework.count_zero"), x, y,
                CONFIG.alertColor,
                highlight && !dismissed);
    }
}