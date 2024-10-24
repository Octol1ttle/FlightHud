package ru.octol1ttle.flightassistant;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import java.awt.Color;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.impl.safety.ElytraStateController;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.HUDConfig;
import ru.octol1ttle.flightassistant.config.IndicatorConfig;
import ru.octol1ttle.flightassistant.config.IndicatorConfigStorage;

public class FAModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            HUDConfig hud = FAConfig.hud();
            IndicatorConfigStorage indicators = FAConfig.getIndicatorConfigStorage();
            ComputerConfig computers = FAConfig.computer();

            return YetAnotherConfigLib.createBuilder()
                    .title(Text.translatable("mod.flightassistant"))
                    .category(hud(Text.translatable("config.flightassistant.category.hud_settings"), hud, new HUDConfig()))
                    .category(indicators(Text.translatable("config.flightassistant.category.not_flying_no_elytra"), indicators.notFlyingNoElytra, IndicatorConfigStorage.createDisabled()))
                    .category(indicators(Text.translatable("config.flightassistant.category.not_flying_has_elytra"), indicators.notFlyingHasElytra, IndicatorConfigStorage.createMinimal()))
                    .category(indicators(Text.translatable("config.flightassistant.category.flying"), indicators.flying, IndicatorConfigStorage.createFull()))
                    .category(computers(Text.translatable("config.flightassistant.category.computer_settings"), computers, new ComputerConfig()))

                    .save(FAConfig::save)

                    .build().generateScreen(parent);
        };
    }

    private ConfigCategory hud(Text name, HUDConfig config, HUDConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(name)

                .option(Option.<Float>createBuilder()
                        .name(Text.translatable("config.flightassistant.hud.scale"))
                        .binding(defaults.hudScale, () -> config.hudScale, o -> config.hudScale = o)
                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                .range(0.1f, 5.0f)
                                .step(0.05f)
                                .formatValue(value -> Text.literal(MathHelper.floor(value * 100.0f) + "%"))
                        )
                        .build()
                )
                .option(Option.<Float>createBuilder()
                        .name(Text.translatable("config.flightassistant.hud.frame_width"))
                        .binding(defaults.frameWidth, () -> config.frameWidth, o -> config.frameWidth = o)
                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                .range(0.3f, 0.9f)
                                .step(0.05f)
                                .formatValue(value -> Text.literal(MathHelper.floor(value * 100.0f) + "%"))
                        )
                        .build()
                )
                .option(Option.<Float>createBuilder()
                        .name(Text.translatable("config.flightassistant.hud.frame_height"))
                        .binding(defaults.frameHeight, () -> config.frameHeight, o -> config.frameHeight = o)
                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                .range(0.3f, 0.9f)
                                .step(0.05f)
                                .formatValue(value -> Text.literal(MathHelper.floor(value * 100.0f) + "%"))
                        )
                        .build()
                )

                .build();
    }

    private ConfigCategory indicators(Text name, IndicatorConfig config, IndicatorConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(name)

                .option(LabelOption.create(Text.translatable("config.flightassistant.indicators.color")))
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.color.frame"))
                        .binding(defaults.frameColor, () -> config.frameColor, o -> config.frameColor = o)
                        .controller(ColorControllerBuilder::create)
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.color.status"))
                        .binding(defaults.statusColor, () -> config.statusColor, o -> config.statusColor = o)
                        .controller(ColorControllerBuilder::create)
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.color.advisory"))
                        .binding(defaults.advisoryColor, () -> config.advisoryColor, o -> config.advisoryColor = o)
                        .controller(ColorControllerBuilder::create)
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.color.caution"))
                        .binding(defaults.cautionColor, () -> config.cautionColor, o -> config.cautionColor = o)
                        .controller(ColorControllerBuilder::create)
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.color.warning"))
                        .binding(defaults.warningColor, () -> config.warningColor, o -> config.warningColor = o)
                        .controller(ColorControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.indicators.speed")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.speed.scale"))
                        .binding(defaults.showSpeedScale, () -> config.showSpeedScale, o -> config.showSpeedScale = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.speed.readout"))
                        .binding(defaults.showSpeedReadout, () -> config.showSpeedReadout, o -> config.showSpeedReadout = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.speed.ground_readout"))
                        .binding(defaults.showGroundSpeedReadout, () -> config.showGroundSpeedReadout, o -> config.showGroundSpeedReadout = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.speed.vertical_readout"))
                        .binding(defaults.showVerticalSpeedReadout, () -> config.showVerticalSpeedReadout, o -> config.showVerticalSpeedReadout = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.indicators.altitude")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.altitude.scale"))
                        .binding(defaults.showAltitudeScale, () -> config.showAltitudeScale, o -> config.showAltitudeScale = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.altitude.readout"))
                        .binding(defaults.showAltitudeReadout, () -> config.showAltitudeReadout, o -> config.showAltitudeReadout = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.altitude.ground"))
                        .binding(defaults.showGroundAltitude, () -> config.showGroundAltitude, o -> config.showGroundAltitude = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.indicators.heading")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.heading.scale"))
                        .binding(defaults.showHeadingScale, () -> config.showHeadingScale, o -> config.showHeadingScale = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.heading.readout"))
                        .binding(defaults.showHeadingReadout, () -> config.showHeadingReadout, o -> config.showHeadingReadout = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.indicators.automation")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.automation.thrust"))
                        .binding(defaults.showFireworkMode, () -> config.showFireworkMode, o -> config.showFireworkMode = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.automation.vertical"))
                        .binding(defaults.showVerticalMode, () -> config.showVerticalMode, o -> config.showVerticalMode = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.automation.lateral"))
                        .binding(defaults.showLateralMode, () -> config.showLateralMode, o -> config.showLateralMode = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.automation.status"))
                        .binding(defaults.showAutomationStatus, () -> config.showAutomationStatus, o -> config.showAutomationStatus = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.indicators.info")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.info.alerts"))
                        .binding(defaults.showAlerts, () -> config.showAlerts, o -> config.showAlerts = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.info.firework_count"))
                        .binding(defaults.showFireworkCount, () -> config.showFireworkCount, o -> config.showFireworkCount = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.info.thrust_setting"))
                        .binding(defaults.showThrustSetting, () -> config.showThrustSetting, o -> config.showThrustSetting = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.info.waypoint_distance"))
                        .binding(defaults.showDistanceToWaypoint, () -> config.showDistanceToWaypoint, o -> config.showDistanceToWaypoint = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.info.waypoint_time"))
                        .binding(defaults.showTimeToWaypoint, () -> config.showTimeToWaypoint, o -> config.showTimeToWaypoint = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.info.flight_phase"))
                        .binding(defaults.showFlightPhase, () -> config.showFlightPhase, o -> config.showFlightPhase = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.indicators.misc")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.misc.attitude"))
                        .binding(defaults.showAttitudeIndicator, () -> config.showAttitudeIndicator, o -> config.showAttitudeIndicator = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.misc.flight_path"))
                        .binding(defaults.showFlightPath, () -> config.showFlightPath, o -> config.showFlightPath = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.misc.coordinates"))
                        .binding(defaults.showCoordinates, () -> config.showCoordinates, o -> config.showCoordinates = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.misc.elytra_health"))
                        .binding(defaults.showElytraHealth, () -> config.showElytraHealth, o -> config.showElytraHealth = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<IndicatorConfig.ElytraHealthDisplayUnits>createBuilder()
                        .name(Text.translatable("config.flightassistant.indicators.misc.elytra_health_units"))
                        .binding(defaults.elytraHealthUnits, () -> config.elytraHealthUnits, o -> config.elytraHealthUnits = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(IndicatorConfig.ElytraHealthDisplayUnits.class))
                        .build()
                )

                .build();
    }

    private ConfigCategory computers(Text name, ComputerConfig config, ComputerConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(name)

                .option(Option.<ComputerConfig.GlobalAutomationsMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.global"))
                        .binding(defaults.globalMode, () -> config.globalMode, o -> config.globalMode = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.GlobalAutomationsMode.class))
                        .build()
                )
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.consider_invulnerability"))
                        .binding(defaults.considerInvulnerability, () -> config.considerInvulnerability, o -> config.considerInvulnerability = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                )

                .option(LabelOption.create(Text.translatable("config.flightassistant.computers.firework")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.firework.lock_unsafe"))
                        .binding(defaults.lockUnsafeFireworks, () -> config.lockUnsafeFireworks, o -> config.lockUnsafeFireworks = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.firework.lock_gpws"))
                        .binding(defaults.lockFireworksFacingTerrain, () -> config.lockFireworksFacingTerrain, o -> config.lockFireworksFacingTerrain = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.computers.stall")))
                .option(Option.<ComputerConfig.WarningMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.stall.warning"))
                        .binding(defaults.stallWarning, () -> config.stallWarning, o -> config.stallWarning = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.WarningMode.class))
                        .build()
                )
                .option(Option.<ComputerConfig.ProtectionMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.stall.protection"))
                        .binding(defaults.stallProtection, () -> config.stallProtection, o -> config.stallProtection = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.ProtectionMode.class))
                        .build()
                )
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.stall.thrust"))
                        .binding(defaults.stallUseThrust, () -> config.stallUseThrust, o -> config.stallUseThrust = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.computers.gpws")))
                .option(Option.<ComputerConfig.WarningMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.gpws.sinkrate.warning"))
                        .binding(defaults.sinkrateWarning, () -> config.sinkrateWarning, o -> config.sinkrateWarning = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.WarningMode.class))
                        .build()
                )
                .option(Option.<ComputerConfig.ProtectionMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.gpws.sinkrate.protection"))
                        .binding(defaults.sinkrateProtection, () -> config.sinkrateProtection, o -> config.sinkrateProtection = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.ProtectionMode.class))
                        .build()
                )
                .option(Option.<ComputerConfig.WarningMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.gpws.terrain.warning"))
                        .binding(defaults.terrainWarning, () -> config.terrainWarning, o -> config.terrainWarning = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.WarningMode.class))
                        .build()
                )
                .option(Option.<ComputerConfig.ProtectionMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.gpws.terrain.protection"))
                        .binding(defaults.terrainProtection, () -> config.terrainProtection, o -> config.terrainProtection = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.ProtectionMode.class))
                        .build()
                )
                .option(Option.<ComputerConfig.WarningMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.gpws.landing.warning"))
                        .binding(defaults.landingClearanceWarning, () -> config.landingClearanceWarning, o -> config.landingClearanceWarning = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.WarningMode.class))
                        .build()
                )

                .option(LabelOption.create(Text.translatable("config.flightassistant.computers.void_level")))
                .option(Option.<ComputerConfig.ProtectionMode>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.void_level.protection"))
                        .binding(defaults.voidProtection, () -> config.voidProtection, o -> config.voidProtection = o)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ComputerConfig.ProtectionMode.class))
                        .build()
                )
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.void_level.thrust"))
                        .binding(defaults.voidUseThrust, () -> config.voidUseThrust, o -> config.voidUseThrust = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.computers.elytra_state")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.elytra_state.close_underwater"))
                        .available(ElytraStateController.isAvailable())
                        .binding(defaults.closeElytraUnderwater, () -> ElytraStateController.isAvailable() && config.closeElytraUnderwater, o -> config.closeElytraUnderwater = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.elytra_state.open_automatically"))
                        .available(ElytraStateController.isAvailable())
                        .binding(defaults.openElytraAutomatically, () -> ElytraStateController.isAvailable() && config.openElytraAutomatically, o -> config.openElytraAutomatically = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(LabelOption.create(Text.translatable("config.flightassistant.computers.chunk_state")))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.flightassistant.computers.chunk_state.preserve_altitude"))
                        .binding(defaults.preserveAltitudeInUnloadedChunk, () -> config.preserveAltitudeInUnloadedChunk, o -> config.preserveAltitudeInUnloadedChunk = o)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .build();
    }
}
