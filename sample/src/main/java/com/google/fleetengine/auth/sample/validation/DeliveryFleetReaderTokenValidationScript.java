package com.google.fleetengine.auth.sample.validation;

import com.google.maps.fleetengine.delivery.v1.TaskTrackingInfo;

/** Validates that fleet reader tokens provide the correct access. */
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
        "Get task tracking info with delivery fleet reader token",
        () -> {
          TaskTrackingInfo unused = commands.getTaskTrackingInfo(trackingId);
        });
  }
}
