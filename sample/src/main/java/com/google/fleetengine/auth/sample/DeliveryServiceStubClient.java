package com.google.fleetengine.auth.sample;

import com.google.fleetengine.auth.sample.validation.DeliveryServiceStub;
import com.google.maps.fleetengine.delivery.v1.CreateDeliveryVehicleRequest;
import com.google.maps.fleetengine.delivery.v1.CreateTaskRequest;
import com.google.maps.fleetengine.delivery.v1.DeliveryServiceClient;
import com.google.maps.fleetengine.delivery.v1.DeliveryVehicle;
import com.google.maps.fleetengine.delivery.v1.GetDeliveryVehicleRequest;
import com.google.maps.fleetengine.delivery.v1.GetTaskRequest;
import com.google.maps.fleetengine.delivery.v1.GetTaskTrackingInfoRequest;
import com.google.maps.fleetengine.delivery.v1.Task;
import com.google.maps.fleetengine.delivery.v1.TaskTrackingInfo;
import com.google.maps.fleetengine.delivery.v1.UpdateTaskRequest;

public class DeliveryServiceStubClient implements DeliveryServiceStub {

  private final DeliveryServiceClient client;

  /** Constructor. */
  public DeliveryServiceStubClient(DeliveryServiceClient client) {
    this.client = client;
  }

  @Override
  public Task createTask(CreateTaskRequest createTaskRequest) {
    return client.createTask(createTaskRequest);
  }

  @Override
  public Task getTask(GetTaskRequest getTaskRequest) {
    return client.getTask(getTaskRequest);
  }

  @Override
  public TaskTrackingInfo getTaskTrackingInfo(
      GetTaskTrackingInfoRequest getTaskTrackingInfoRequest) {
    return client.getTaskTrackingInfo(getTaskTrackingInfoRequest);
  }

  @Override
  public Task updateTask(UpdateTaskRequest updateTaskRequest) {
    return client.updateTask(updateTaskRequest);
  }

  @Override
  public DeliveryVehicle createDeliveryVehicle(
      CreateDeliveryVehicleRequest createDeliveryVehicleRequest) {
    return client.createDeliveryVehicle(createDeliveryVehicleRequest);
  }

  @Override
  public DeliveryVehicle getDeliveryVehicle(GetDeliveryVehicleRequest getDeliveryVehicleRequest) {
    return client.getDeliveryVehicle(getDeliveryVehicleRequest);
  }
}
