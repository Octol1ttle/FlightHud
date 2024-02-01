package ru.octol1ttle.flightassistant.computers.navigation;

import java.util.ArrayList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

public class FlightPlanner extends ArrayList<Waypoint> implements ITickableComputer {
    private final AirDataComputer data;
    private @Nullable Waypoint targetWaypoint;

    public FlightPlanner(AirDataComputer data) {
        this.data = data;
    }

    @Override
    public void tick() {
        if (!this.contains(targetWaypoint)) {
            targetWaypoint = null;
        }

        if (targetWaypoint == null) {
            return;
        }

        Vector2d target = new Vector2d(targetWaypoint.targetPosition());
        float altitude = targetWaypoint.targetAltitude() != null ? targetWaypoint.targetAltitude() : data.altitude;
        if (target.sub(data.position.x, data.position.z).length() <= 20.0f && Math.abs(altitude - data.altitude) <= 10.0f) {
            int nextIndex = this.indexOf(targetWaypoint) + 1;
            targetWaypoint = waypointExistsAt(nextIndex) ? this.get(nextIndex) : null;
        }
    }

    public boolean waypointExistsAt(int index) {
        return index < this.size();
    }

    public @Nullable Integer getManagedSpeed() {
        if (targetWaypoint == null) {
            return null;
        }
        return targetWaypoint.targetSpeed();
    }

    public @Nullable Integer getManagedAltitude() {
        if (targetWaypoint == null) {
            return null;
        }
        return targetWaypoint.targetAltitude();
    }

    public @Nullable Float getManagedHeading() {
        if (targetWaypoint == null) {
            return null;
        }

        Vec3d current = data.position;
        Vector2d target = targetWaypoint.targetPosition();
        // this is SO FUCKING STUPID, I have ZERO IDEA how to do it PROPERLY without everything breaking. why couldn't Mojang FOLLOW THE GOD DAMN STANDARDS
        return AirDataComputer.toHeading((float) Math.toDegrees(MathHelper.atan2(-(target.x - current.x), target.y - current.z)));
    }

    public @Nullable Vector2d getTargetPosition() {
        if (targetWaypoint == null) {
            return null;
        }

        return targetWaypoint.targetPosition();
    }

    public void execute(int waypointIndex) {
        // TODO: throw exception if waypoint doesn't exist
        if (waypointExistsAt(waypointIndex)) {
            targetWaypoint = this.get(waypointIndex);
        }
    }

    @Override
    public void reset() {
        targetWaypoint = null;
    }

    @Override
    public String getId() {
        return "flt_plan";
    }
}
