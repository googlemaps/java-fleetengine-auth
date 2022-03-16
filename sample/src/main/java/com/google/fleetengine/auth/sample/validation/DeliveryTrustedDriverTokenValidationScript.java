package com.google.fleetengine.auth.sample.validation;

import com.google.fleetengine.auth.token.DeliveryVehicleClaims;

public class DeliveryTrustedDriverTokenValidationScript {

  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  public DeliveryTrustedDriverTokenValidationScript(
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
                    .getTrustedDeliveryVehicleToken(
                        DeliveryVehicleClaims.create(deliveryVehicleId)));

    runtime.runCommand(
        "Get delivery vehicle with trusted driver token",
        () -> commands.getDeliveryVehicle(deliveryVehicleId));

    runtime.expectPermissionDenied(
        "Get delivery vehicle fails when delivery vehicle id different than "
            + "trusted driver token",
        () -> commands.getDeliveryVehicle(ScriptUtils.generateRandomDeliveryVehicleId()));

    String newDeliveryVehicleId = ScriptUtils.generateRandomDeliveryVehicleId();
    DeliveryServiceCommands deliveryCommands =
        commandsFactory.createDeliveryServiceCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () ->
                configuration
                    .getMinter()
                    .getTrustedDeliveryVehicleToken(
                        DeliveryVehicleClaims.create(newDeliveryVehicleId)));

    runtime.runCommand(
        "Create delivery vehicle with trusted driver token",
        () -> deliveryCommands.createDeliveryVehicle(newDeliveryVehicleId));
  }
}
