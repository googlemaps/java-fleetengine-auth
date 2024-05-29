package com.google.fleetengine.auth.sample.validation;

import com.google.maps.fleetengine.delivery.v1.CreateDeliveryVehicleRequest;
import com.google.maps.fleetengine.delivery.v1.CreateTaskRequest;
import com.google.maps.fleetengine.delivery.v1.DeliveryVehicle;
import com.google.maps.fleetengine.delivery.v1.GetDeliveryVehicleRequest;
import com.google.maps.fleetengine.delivery.v1.GetTaskRequest;
import com.google.maps.fleetengine.delivery.v1.GetTaskTrackingInfoRequest;
import com.google.maps.fleetengine.delivery.v1.Task;
import com.google.maps.fleetengine.delivery.v1.TaskTrackingInfo;
import com.google.maps.fleetengine.delivery.v1.UpdateTaskRequest;

/** Wraps {@link com.google.maps.fleetengine.delivery.v1.DeliveryServiceClient} for testing purposes. */
public interface DeliveryServiceStub {

  /**
   * see: {@link
   * com.google.maps.fleetengine.delivery.v1.DeliveryServiceClient#createTask(CreateTaskRequest)}
   */
  Task createTask(CreateTaskRequest createTaskRequest);

  /**
   * see: {@link com.google.maps.fleetengine.delivery.v1.DeliveryServiceClient#getTask(GetTaskRequest)}
   */
  Task getTask(GetTaskRequest getTripRequest);

  /**
   * see: {@link
   * com.google.maps.fleetengine.delivery.v1.DeliveryServiceClient#getTaskTrackingInfo(GetTaskTrackingInfoRequest)}
   */
  TaskTrackingInfo getTaskTrackingInfo(GetTaskTrackingInfoRequest getTaskTrackingInfoRequest);

  /**
   * see: {@link
   * com.google.maps.fleetengine.delivery.v1.DeliveryServiceClient#updateTask(UpdateTaskRequest)}
   */
  Task updateTask(UpdateTaskRequest updateTaskRequest);

  /**
   * see: {@link
   * com.google.maps.fleetengine.delivery.v1.DeliveryServiceClient#createDeliveryVehicle(CreateDeliveryVehicleRequest)}
   */
  DeliveryVehicle createDeliveryVehicle(CreateDeliveryVehicleRequest createDeliveryVehicleRequest);

  /**
   * see: {@link
   * com.google.maps.fleetengine.delivery.v1.DeliveryServiceClient#getDeliveryVehicle(GetDeliveryVehicleRequest)}
   */
  DeliveryVehicle getDeliveryVehicle(GetDeliveryVehicleRequest deliveryVehicleRequest);
}
