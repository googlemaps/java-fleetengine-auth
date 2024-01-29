package com.google.fleetengine.auth.sample.validation;

/** Validates that fleet reader tokens provide the correct access. */
public class DeliveryDriverFleetReaderTokenValidationScript {

  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  public DeliveryDriverFleetReaderTokenValidationScript(
      SampleScriptRuntime runtime,
      SampleScriptConfiguration configuration,
      CommandsFactory commandsFactory) {
    this.runtime = runtime;
    this.configuration = configuration;
    this.commandsFactory = commandsFactory;
  }

  /**
   * Run validation script.
   *
   * @param deliveryVehicleId existing delivery vehicle id
   */
  public void run(String deliveryVehicleId) throws Throwable {
    // Tokens are minted with the fleet reader role and authorized to access deliveryVehicleId.
    DeliveryServiceCommands commands =
        commandsFactory.createDeliveryServiceCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () -> configuration.getMinter().getFleetReaderToken());

    runtime.runCommand(
        "Get delivery vehicle with fleet reader token",
        () -> commands.getDeliveryVehicle(deliveryVehicleId));
  }
}
