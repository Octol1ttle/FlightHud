package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.safety.GroundProximityComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;


public class AutoFlightComputer implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final GroundProximityComputer gpws = ComputerRegistry.resolve(GroundProximityComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
    private final FireworkController firework = ComputerRegistry.resolve(FireworkController.class);

    public boolean flightDirectorsEnabled = false;
    public boolean autoFireworkEnabled = false;
    public boolean autoPilotEnabled = false;

    public boolean afrwkDisconnectionForced = false;
    public boolean apDisconnectionForced = false;

    public Integer selectedSpeed;
    public Integer selectedAltitude;
    public Integer selectedHeading;

    @Override
    public void tick() {
        if (autoFireworkEnabled && data.isCurrentChunkLoaded && gpws.fireworkUseSafe && gpws.getGPWSLampColor() == FAConfig.indicator().frameColor) {
            Integer targetSpeed = getTargetSpeed();
            Integer targetAltitude = getTargetAltitude();
            if (targetSpeed != null) {
                if (data.speed() < targetSpeed) {
                    firework.activateFirework(false);
                }
            } else if (targetAltitude != null && targetAltitude > data.altitude()
                    && data.speed() < 30
                    && data.velocity.y < 0.0f
                    && data.pitch() > 0) {
                firework.activateFirework(false);
            }
        }
    }

    public @Nullable Integer getTargetSpeed() {
        return selectedSpeed != null ? selectedSpeed : plan.getManagedSpeed();
    }

    public @Nullable Integer getTargetAltitude() {
        return selectedAltitude != null ? selectedAltitude : plan.getManagedAltitude();
    }

    public @Nullable Float getTargetHeading() {
        return selectedHeading != null ? Float.valueOf(selectedHeading) : plan.getManagedHeading();
    }

    public void disconnectAutopilot(boolean force) {
        if (autoPilotEnabled) {
            autoPilotEnabled = false;
            apDisconnectionForced = force;
        }
    }

    public void disconnectAutoFirework(boolean force) {
        if (autoFireworkEnabled) {
            autoFireworkEnabled = false;
            afrwkDisconnectionForced = force;
        }
    }

    @Override
    public String getId() {
        return "auto_flt";
    }

    @Override
    public void reset() {
        flightDirectorsEnabled = false;
        disconnectAutoFirework(true);
        disconnectAutopilot(true);
    }
}
