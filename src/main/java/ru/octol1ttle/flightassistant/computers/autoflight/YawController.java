package ru.octol1ttle.flightassistant.computers.autoflight;

import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.IRenderTickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;

public class YawController implements IRenderTickableComputer {
    private final TimeComputer time;
    private final AirDataComputer data;

    public Float targetHeading;

    public YawController(TimeComputer time, AirDataComputer data) {
        this.time = time;
        this.data = data;
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        smoothSetHeading(targetHeading, time.deltaTime);
    }

    private void smoothSetHeading(Float heading, float delta) {
        if (heading == null) {
            return;
        }

        data.player.setYaw(data.player.getYaw() + (heading - data.heading) * delta);
    }

    @Override
    public String getId() {
        return "yaw_ctl";
    }

    @Override
    public void reset() {
        targetHeading = null;
    }
}
