package com.google.fleetengine.auth.sample.validation;

/** Validates that fleet reader tokens provide the correct access. */
public class DeliveryConsumerFleetReaderTokenValidationScript {

  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  public DeliveryConsumerFleetReaderTokenValidationScript(
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
   * @param trackingId existing tracking id
   */
  public void run(String trackingId) throws Throwable {
    // Tokens are minted with the fleet reader role and authorized to access trackingId.
    DeliveryServiceCommands commands =
        commandsFactory.createDeliveryServiceCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () -> configuration.getMinter().getFleetReaderToken());

    runtime.runCommand(
        "Search tasks with fleet reader token", () -> commands.searchTasks(trackingId));
  }
}
