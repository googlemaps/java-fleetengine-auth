package com.google.fleetengine.auth.sample.validation;

import google.maps.fleetengine.delivery.v1.CreateDeliveryVehicleRequest;
import google.maps.fleetengine.delivery.v1.CreateTaskRequest;
import google.maps.fleetengine.delivery.v1.DeliveryVehicle;
import google.maps.fleetengine.delivery.v1.GetDeliveryVehicleRequest;
import google.maps.fleetengine.delivery.v1.GetTaskRequest;
import google.maps.fleetengine.delivery.v1.SearchTasksRequest;
import google.maps.fleetengine.delivery.v1.Task;
import google.maps.fleetengine.delivery.v1.UpdateTaskRequest;

/** Wraps {@link google.maps.fleetengine.delivery.v1.DeliveryServiceClient} for testing purposes. */
public interface DeliveryServiceStub {

  /**
   * see: {@link
   * google.maps.fleetengine.delivery.v1.DeliveryServiceClient#createTask(CreateTaskRequest)}
   */
  Task createTask(CreateTaskRequest createTaskRequest);

  /**
   * see: {@link google.maps.fleetengine.delivery.v1.DeliveryServiceClient#getTask(GetTaskRequest)}
   */
  Task getTask(GetTaskRequest getTripRequest);

  /**
   * see: {@link
   * google.maps.fleetengine.delivery.v1.DeliveryServiceClient#searchTasks(SearchTasksRequest)}
   */
  void searchTasks(SearchTasksRequest searchTripRequest);

  /**
   * see: {@link
   * google.maps.fleetengine.delivery.v1.DeliveryServiceClient#updateTask(UpdateTaskRequest)}
   */
  Task updateTask(UpdateTaskRequest updateTaskRequest);

  /**
   * see: {@link
   * google.maps.fleetengine.delivery.v1.DeliveryServiceClient#createDeliveryVehicle(CreateDeliveryVehicleRequest)}
   */
  DeliveryVehicle createDeliveryVehicle(CreateDeliveryVehicleRequest createDeliveryVehicleRequest);

  /**
   * see: {@link
   * google.maps.fleetengine.delivery.v1.DeliveryServiceClient#getDeliveryVehicle(GetDeliveryVehicleRequest)}
   */
  DeliveryVehicle getDeliveryVehicle(GetDeliveryVehicleRequest deliveryVehicleRequest);
}
