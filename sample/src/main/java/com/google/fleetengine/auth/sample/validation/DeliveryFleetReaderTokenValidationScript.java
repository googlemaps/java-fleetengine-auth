package com.google.fleetengine.auth.sample.validation;

public class DeliveryFleetReaderTokenValidationScript {
  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  /** Constructor. */
  public DeliveryFleetReaderTokenValidationScript(
      SampleScriptRuntime runtime,
      SampleScriptConfiguration configuration,
      CommandsFactory commandsFactory) {
    this.runtime = runtime;
    this.configuration = configuration;
    this.commandsFactory = commandsFactory;
  }

  /** Run validation script. */
  public void run(String trackingId) throws Throwable {
    DeliveryServiceCommands commands =
        commandsFactory.createDeliveryServiceCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () -> configuration.getMinter().getDeliveryFleetReaderToken());

    runtime.runCommand(
        "Search tasks with fleet reader token", () -> commands.searchTasks(trackingId));
  }
}
