package ru.octol1ttle.flightassistant.alerts.fault;

import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;

public abstract class ComputerFaultAlert extends BaseAlert implements IECAMAlert {
    /*private final ComputerHost host;

    public ComputerFaultAlert(ComputerHost host) {
        this.host = host;
    }

    @Override
    public boolean isTriggered() {
        return !host.faulted.isEmpty();
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.MASTER_WARNING;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        int i = 0;
        for (IComputer computer : host.faulted) {
            i += DrawHelper.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.fault.computers." + computer.getId()), x, y,
                    FAConfig.indicator().warningColor, highlight);
            y += 10;
        }

        return i;
    }*/
}
