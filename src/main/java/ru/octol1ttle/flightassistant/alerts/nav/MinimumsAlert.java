package ru.octol1ttle.flightassistant.alerts.nav;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class MinimumsAlert extends AbstractAlert {
    private final AirDataComputer data;
    private final FlightPlanner plan;

    public MinimumsAlert(AirDataComputer data, FlightPlanner plan) {
        this.data = data;
        this.plan = plan;
    }

    @Override
    public boolean isTriggered() {
        Integer minimums = plan.getMinimums(data.groundLevel);
        return data.isFlying && plan.landingInProgress && minimums != null && data.altitude <= minimums;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.MINIMUMS;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        return HudComponent.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.reached_minimums"), x, y,
                FAConfig.hud().cautionColor, false);
    }
}