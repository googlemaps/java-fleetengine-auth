package com.google.fleetengine.auth.sample.validation;

import com.google.protobuf.Duration;
import com.google.protobuf.FieldMask;
import com.google.type.LatLng;
import google.maps.fleetengine.delivery.v1.CreateDeliveryVehicleRequest;
import google.maps.fleetengine.delivery.v1.CreateTaskRequest;
import google.maps.fleetengine.delivery.v1.DeliveryVehicle;
import google.maps.fleetengine.delivery.v1.GetDeliveryVehicleRequest;
import google.maps.fleetengine.delivery.v1.GetTaskRequest;
import google.maps.fleetengine.delivery.v1.LocationInfo;
import google.maps.fleetengine.delivery.v1.SearchTasksRequest;
import google.maps.fleetengine.delivery.v1.Task;
import google.maps.fleetengine.delivery.v1.Task.State;
import google.maps.fleetengine.delivery.v1.Task.TaskOutcome;
import google.maps.fleetengine.delivery.v1.UpdateTaskRequest;

public class DeliveryServiceCommands {
  private static final LatLng PLANNED_LOCATION = ProtosUtil.getLatLng(37.419646, -122.073885);

  private final DeliveryServiceStub stub;
  private final String providerId;

  /** Constructor. */
  public DeliveryServiceCommands(DeliveryServiceStub stub, String providerId) {
    this.stub = stub;
    this.providerId = providerId;
  }

  /** Create trip with a given id and hardcoded values. */
  public Task createTask(String taskId, String trackingId) {
    Task task =
        Task.newBuilder()
            .setName(getTaskName(taskId))
            .setTrackingId(trackingId)
            .setPlannedLocation(LocationInfo.newBuilder().setPoint(PLANNED_LOCATION).build())
            .setTaskDuration(Duration.newBuilder().setSeconds(3600).build())
            .setType(Task.Type.DELIVERY)
            .setState(State.OPEN)
            .build();

    CreateTaskRequest createTaskRequest =
        CreateTaskRequest.newBuilder()
            .setTask(task)
            .setTaskId(taskId)
            .setParent(ScriptUtils.getProviderName(providerId))
            .build();
    return stub.createTask(createTaskRequest);
  }

  /** Get existing task. */
  public Task getTask(String taskId) {
    GetTaskRequest getTaskRequest =
        GetTaskRequest.newBuilder().setName(getTaskName(taskId)).build();
    return stub.getTask(getTaskRequest);
  }

  /** Search existing tasks by tracking id. */
  public void searchTasks(String trackingId) {
    SearchTasksRequest searchTasksRequest =
        SearchTasksRequest.newBuilder()
            .setParent(ScriptUtils.getProviderName(providerId))
            .setTrackingId(trackingId)
            .build();
    stub.searchTasks(searchTasksRequest);
  }

  /** Update task outcome to succeed. */
  public Task completeTask(String taskId) {
    UpdateTaskRequest updateTaskRequest =
        UpdateTaskRequest.newBuilder()
            .setUpdateMask(FieldMask.newBuilder().addPaths("task_outcome").build())
            .setTask(
                Task.newBuilder()
                    .setName(getTaskName(taskId))
                    .setTaskOutcome(TaskOutcome.SUCCEEDED)
                    .build())
            .build();
    return stub.updateTask(updateTaskRequest);
  }

  /** Task names must be in the given format. */
  private String getTaskName(String taskId) {
    return String.format("providers/%s/tasks/%s", providerId, taskId);
  }

  public DeliveryVehicle createDeliveryVehicle(String deliveryVehicleId) {
    DeliveryVehicle deliveryVehicle =
        DeliveryVehicle.newBuilder().setName(getDeliveryVehicleName(deliveryVehicleId)).build();

    CreateDeliveryVehicleRequest createDeliveryVehicleRequest =
        CreateDeliveryVehicleRequest.newBuilder()
            .setDeliveryVehicle(deliveryVehicle)
            .setDeliveryVehicleId(deliveryVehicleId)
            .setParent(ScriptUtils.getProviderName(providerId))
            .build();
    return stub.createDeliveryVehicle(createDeliveryVehicleRequest);
  }

  public DeliveryVehicle getDeliveryVehicle(String deliveryVehicleId) {
    GetDeliveryVehicleRequest getDeliveryVehicleRequest =
        GetDeliveryVehicleRequest.newBuilder()
            .setName(getDeliveryVehicleName(deliveryVehicleId))
            .build();
    return stub.getDeliveryVehicle(getDeliveryVehicleRequest);
  }

  /** Delivery vehicle names must be in the given format. */
  private String getDeliveryVehicleName(String deliveryVehicleId) {
    return String.format("providers/%s/deliveryVehicles/%s", providerId, deliveryVehicleId);
  }
}
