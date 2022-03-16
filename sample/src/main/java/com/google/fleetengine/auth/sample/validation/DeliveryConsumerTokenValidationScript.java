package com.google.fleetengine.auth.sample.validation;

import com.google.fleetengine.auth.token.TrackingClaims;

public class DeliveryConsumerTokenValidationScript {
  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  /** Constructor. */
  public DeliveryConsumerTokenValidationScript(
      SampleScriptRuntime runtime,
      SampleScriptConfiguration configuration,
      CommandsFactory commandsFactory) {
    this.runtime = runtime;
    this.configuration = configuration;
    this.commandsFactory = commandsFactory;
  }

  /** Run validation script. */
  public void run(String trackingId) throws Throwable {
    DeliveryServiceCommands taskCommandsWithTrackingId =
        commandsFactory.createDeliveryServiceCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () ->
                configuration
                    .getMinter()
                    .getDeliveryConsumerToken(TrackingClaims.create(trackingId)));

    runtime.runCommand(
        "Search tasks with delivery consumer token",
        () -> taskCommandsWithTrackingId.searchTasks(trackingId));

    DeliveryServiceCommands taskCommandsWithIncorrectTrackingId =
        commandsFactory.createDeliveryServiceCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () ->
                configuration
                    .getMinter()
                    .getDeliveryConsumerToken(
                        TrackingClaims.create(ScriptUtils.generateRandomTrackingId())));

    runtime.expectPermissionDenied(
        "Search task fails when tracking id of task different than delivery " + "consumer token",
        () -> taskCommandsWithIncorrectTrackingId.searchTasks(trackingId));
  }
}
