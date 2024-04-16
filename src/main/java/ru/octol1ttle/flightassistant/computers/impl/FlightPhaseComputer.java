package ru.octol1ttle.flightassistant.computers.impl;

import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FlightPhaseComputer implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightComputer autoflight = ComputerRegistry.resolve(AutoFlightComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
    public FlightPhase phase = FlightPhase.UNKNOWN;
    private float minimumHeight = Float.MAX_VALUE;
    private boolean wasAutolandAllowed = true;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void tick() {
        if (data.player().isOnGround()) {
            phase = FlightPhase.ON_GROUND;
        }

        if (!isAboutToLand()) {
            minimumHeight = Float.MAX_VALUE;
        }

        if (!data.isFlying()) {
            return;
        }

        Integer cruiseAltitude = plan.getCruiseAltitude();
        Integer targetAltitude = autoflight.getTargetAltitude();
        if (cruiseAltitude == null || targetAltitude == null) {
            phase = FlightPhase.UNKNOWN;
            return;
        }

        if (phase == FlightPhase.ON_GROUND) {
            phase = FlightPhase.TAKEOFF;
        }

        if (phase == FlightPhase.TAKEOFF && data.heightAboveGround() <= 10.0f) {
            return;
        }

        if (!isNearDestination()) {
            if (data.altitude() - targetAltitude <= 5.0f) {
                phase = FlightPhase.CLIMB;
            } else {
                phase = FlightPhase.DESCENT;
            }

            if (targetAltitude.equals(cruiseAltitude) && Math.abs(cruiseAltitude - data.altitude()) <= 5.0f) {
                phase = FlightPhase.CRUISE;
            }
        }

        if (phase == FlightPhase.GO_AROUND) {
            if (plan.getDistanceToWaypoint() > 150.0f) {
                phase = FlightPhase.APPROACH;
            }
        } else {
            if (plan.isOnApproach()) {
                phase = FlightPhase.APPROACH;
            }

            if (phase == FlightPhase.APPROACH && plan.autolandAllowed) {
                phase = FlightPhase.LAND;
            }
        }

        if (isAboutToLand() && plan.landAltitude != null) {
            float heightAboveDestination = data.altitude() - plan.landAltitude;
            minimumHeight = MathHelper.clamp(heightAboveDestination, 0.0f, minimumHeight);

            if (heightAboveDestination - minimumHeight >= 5.0f || (wasAutolandAllowed && !plan.autolandAllowed)) {
                phase = FlightPhase.GO_AROUND;
            }
        }

        wasAutolandAllowed = plan.autolandAllowed;
    }

    private boolean isAboutToLand() {
        return phase == FlightPhase.APPROACH || phase == FlightPhase.LAND;
    }

    private boolean isNearDestination() {
        return isAboutToLand() || phase == FlightPhase.GO_AROUND;
    }

    @Override
    public String getId() {
        return "flight_phase";
    }

    @Override
    public void reset() {
        phase = FlightPhase.UNKNOWN;
        minimumHeight = Float.MAX_VALUE;
        wasAutolandAllowed = true;
    }

    public enum FlightPhase {
        ON_GROUND("on_ground"),
        TAKEOFF("takeoff"),
        CLIMB("climb"),
        CRUISE("cruise"),
        DESCENT("descent"),
        APPROACH("approach"),
        LAND("land"),
        GO_AROUND("go_around"),
        UNKNOWN("");

        public final Text text;

        FlightPhase(Text text) {
            this.text = text;
        }

        FlightPhase(String key) {
            this(Text.translatable("status.flightassistant.phase." + key));
        }
    }
}
