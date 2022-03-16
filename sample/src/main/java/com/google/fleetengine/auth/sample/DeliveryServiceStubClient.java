package com.google.fleetengine.auth.sample;

import com.google.fleetengine.auth.sample.validation.DeliveryServiceStub;
import google.maps.fleetengine.delivery.v1.CreateDeliveryVehicleRequest;
import google.maps.fleetengine.delivery.v1.CreateTaskRequest;
import google.maps.fleetengine.delivery.v1.DeliveryServiceClient;
import google.maps.fleetengine.delivery.v1.DeliveryVehicle;
import google.maps.fleetengine.delivery.v1.GetDeliveryVehicleRequest;
import google.maps.fleetengine.delivery.v1.GetTaskRequest;
import google.maps.fleetengine.delivery.v1.SearchTasksRequest;
import google.maps.fleetengine.delivery.v1.Task;
import google.maps.fleetengine.delivery.v1.UpdateTaskRequest;

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
  public void searchTasks(SearchTasksRequest searchTripRequest) {
    client.searchTasks(searchTripRequest);
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
