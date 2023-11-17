package ru.octol1ttle.flightassistant.alerts.other;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.ECAMSoundData;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;

public class ElytraHealthLowAlert extends AbstractAlert {
    private final AirDataComputer data;

    public ElytraHealthLowAlert(AirDataComputer data) {
        this.data = data;
    }

    @Override
    public boolean isTriggered() {
        return data.elytraHealth != null && data.elytraHealth <= 5.0f;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return ECAMSoundData.MASTER_WARNING;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedFont(textRenderer, context, Text.translatable("alerts.flightassistant.elytra_health_low"), x, y,
                HudComponent.CONFIG.alertColor,
                !dismissed && highlight);
    }
}