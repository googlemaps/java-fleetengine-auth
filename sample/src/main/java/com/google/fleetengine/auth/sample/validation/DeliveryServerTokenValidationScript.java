package com.google.fleetengine.auth.sample.validation;

public class DeliveryServerTokenValidationScript {
  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  /** Constructor */
  public DeliveryServerTokenValidationScript(
      SampleScriptRuntime runtime,
      SampleScriptConfiguration configuration,
      CommandsFactory commandsFactory) {
    this.runtime = runtime;
    this.configuration = configuration;
    this.commandsFactory = commandsFactory;
  }

  public Ids run() throws Throwable {
    Ids ids =
        new Ids(
            ScriptUtils.generateRandomTaskId(),
            ScriptUtils.generateRandomDeliveryVehicleId(),
            ScriptUtils.generateRandomTrackingId());

    DeliveryServiceCommands commands =
        commandsFactory.createDeliveryServiceCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            configuration.getMinter());

    runtime.runCommand(
        "Create delivery vehicle with server token",
        () -> commands.createDeliveryVehicle(ids.getDeliveryVehicleId()));

    runtime.runCommand(
        "Get delivery vehicle with server token",
        () -> commands.getDeliveryVehicle(ids.getDeliveryVehicleId()));

    runtime.runCommand(
        "Create task with server token",
        () -> commands.createTask(ids.getTaskId(), ids.trackingId));

    runtime.runCommand(
        "Complete task with server token", () -> commands.completeTask(ids.getTaskId()));

    return ids;
  }

  /** Ids of newly created entities. */
  public static class Ids {

    private final String deliveryVehicleId;
    private final String taskId;
    private final String trackingId;

    Ids(String deliveryVehicleId, String taskId, String trackingId) {
      this.deliveryVehicleId = deliveryVehicleId;
      this.taskId = taskId;
      this.trackingId = trackingId;
    }

    /** Id of the new Delivery Vehicle created. */
    public String getDeliveryVehicleId() {
      return deliveryVehicleId;
    }

    /** Id of the new Task created. */
    public String getTaskId() {
      return taskId;
    }

    /** Tracking id of the new task created. */
    public String getTrackingId() {
      return trackingId;
    }
  }
}
