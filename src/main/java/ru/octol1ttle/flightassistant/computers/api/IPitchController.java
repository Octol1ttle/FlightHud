package ru.octol1ttle.flightassistant.computers.api;

import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IPitchController {
    /**
     * Gets the target pitch that this controller wants
     * @return a pair of two floats: the first one is the target pitch, second is the delta time multiplier. If 'null', the controller doesn't want any pitch at this moment
     */
    @Nullable
    Pair<@NotNull Float, @NotNull Float> getControlledPitch();

    ControllerPriority getPriority();
}