package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.api.ControllerPriority;
import ru.octol1ttle.flightassistant.computers.api.IAutopilotProvider;
import ru.octol1ttle.flightassistant.computers.api.IPitchController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.IHeadingController;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.FlightPhaseComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AutopilotControlComputer implements ITickableComputer, IPitchController, IHeadingController, IAutopilotProvider {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final AutoFlightComputer autoflight = ComputerRegistry.resolve(AutoFlightComputer.class);
    private final FlightPhaseComputer phase = ComputerRegistry.resolve(FlightPhaseComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
    public Float targetPitch;
    public Float targetHeading;

    @Override
    public void tick() {
        targetPitch = computeTargetPitch();
        targetHeading = computeTargetHeading();
    }

    private Float computeTargetPitch() {
        if (phase.phase == FlightPhaseComputer.FlightPhase.TAKEOFF
                || phase.phase == FlightPhaseComputer.FlightPhase.GO_AROUND && data.heightAboveGround() < 15.0f) {
            return PitchController.CLIMB_PITCH;
        }

        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude == null) {
            return null;
        }
        Vector2d planPos = plan.getTargetPosition();

        float diff = targetAltitude - data.altitude();
        boolean landing = phase.phase == FlightPhaseComputer.FlightPhase.LAND;
        if (!landing && diff > -10.0f && diff < 5.0f) {
            return (PitchController.GLIDE_PITCH + PitchController.ALTITUDE_PRESERVE_PITCH) * 0.5f;
        }

        if (data.altitude() < targetAltitude) {
            return computeClimbPitch(diff, planPos);
        }

        if (planPos == null || autoflight.selectedAltitude != null) {
            if (diff > -15.0f) {
                return PitchController.GLIDE_PITCH;
            }

            return (PitchController.GLIDE_PITCH + PitchController.DESCEND_PITCH) * 0.5f;
        }

        float degrees = FAMathHelper.toDegrees(
                MathHelper.atan2(
                        diff,
                        Vector2d.distance(data.position().x, data.position().z, planPos.x, planPos.y)
                )
        );
        if (!landing && diff > -15.0f) {
            return Math.max(PitchController.GLIDE_PITCH, degrees);
        }

        return degrees;
    }

    private float computeClimbPitch(float diff, Vector2d target) {
        if (target == null || autoflight.selectedAltitude != null) {
            if (diff <= 15.0f) {
                return PitchController.ALTITUDE_PRESERVE_PITCH;
            }

            return (PitchController.ALTITUDE_PRESERVE_PITCH + PitchController.CLIMB_PITCH) * 0.5f;
        }

        float degrees = Math.max(5.0f, FAMathHelper.toDegrees(
                MathHelper.atan2(
                        diff,
                        Vector2d.distance(data.position().x, data.position().z, target.x, target.y)
                )
        ));
        if (diff <= 15.0f) {
            return Math.min(PitchController.ALTITUDE_PRESERVE_PITCH, degrees);
        }

        return degrees;
    }

    private Float computeTargetHeading() {
        if (phase.phase == FlightPhaseComputer.FlightPhase.TAKEOFF || phase.phase == FlightPhaseComputer.FlightPhase.GO_AROUND) {
            return data.heading();
        }

        return autoflight.getTargetHeading();
    }

    @Override
    public @Nullable Pair<@NotNull Float, @NotNull Float> getControlledPitch() {
        if (!autoflight.autoPilotEnabled || targetPitch == null) {
            return null;
        }

        return new Pair<>(targetPitch, 1.0f);
    }

    @Override
    public @Nullable Pair<@NotNull Float, @NotNull Float> getControlledHeading() {
        if (!autoflight.autoPilotEnabled || targetHeading == null) {
            return null;
        }

        return new Pair<>(targetHeading, 1.0f);
    }

    @Override
    public ControllerPriority getPriority() {
        return ControllerPriority.NORMAL;
    }

    @Override
    public String getId() {
        return "autopilot_ctl";
    }

    @Override
    public void reset() {
        targetPitch = null;
        targetHeading = null;

        autoflight.disconnectAutoFirework(true);
        autoflight.disconnectAutopilot(true);
    }
}