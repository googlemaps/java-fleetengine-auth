package com.google.fleetengine.auth.sample.validation;

import com.google.fleetengine.auth.token.DeliveryVehicleClaims;

public class DeliveryUntrustedDriverTokenValidationScript {
  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  /** Constructor */
  public DeliveryUntrustedDriverTokenValidationScript(
      SampleScriptRuntime runtime,
      SampleScriptConfiguration configuration,
      CommandsFactory commandsFactory) {
    this.runtime = runtime;
    this.configuration = configuration;
    this.commandsFactory = commandsFactory;
  }

  public void run(String deliveryVehicleId) throws Throwable {
    DeliveryServiceCommands commands =
        commandsFactory.createDeliveryServiceCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () ->
                configuration
                    .getMinter()
                    .getUntrustedDeliveryVehicleToken(
                        DeliveryVehicleClaims.create(deliveryVehicleId)));

    runtime.runCommand(
        "Get delivery vehicle with untrusted driver token",
        () -> commands.getDeliveryVehicle(deliveryVehicleId));

    runtime.expectPermissionDenied(
        "Get delivery vehicle fails when delivery vehicle id different than "
            + "untrusted driver token",
        () -> commands.getDeliveryVehicle(ScriptUtils.generateRandomDeliveryVehicleId()));

    runtime.expectPermissionDenied(
        "Create delivery vehicle fails with untrusted driver token",
        () -> commands.createDeliveryVehicle(ScriptUtils.generateRandomDeliveryVehicleId()));
  }
}
