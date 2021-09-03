// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/maps/fleetengine/v1/trip_api.proto

package google.maps.fleetengine.v1;

public interface SearchTripsResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:maps.fleetengine.v1.SearchTripsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The list of trips for the requested vehicle.
   * </pre>
   *
   * <code>repeated .maps.fleetengine.v1.Trip trips = 1;</code>
   */
  java.util.List<google.maps.fleetengine.v1.Trip> getTripsList();
  /**
   *
   *
   * <pre>
   * The list of trips for the requested vehicle.
   * </pre>
   *
   * <code>repeated .maps.fleetengine.v1.Trip trips = 1;</code>
   */
  google.maps.fleetengine.v1.Trip getTrips(int index);
  /**
   *
   *
   * <pre>
   * The list of trips for the requested vehicle.
   * </pre>
   *
   * <code>repeated .maps.fleetengine.v1.Trip trips = 1;</code>
   */
  int getTripsCount();
  /**
   *
   *
   * <pre>
   * The list of trips for the requested vehicle.
   * </pre>
   *
   * <code>repeated .maps.fleetengine.v1.Trip trips = 1;</code>
   */
  java.util.List<? extends google.maps.fleetengine.v1.TripOrBuilder> getTripsOrBuilderList();
  /**
   *
   *
   * <pre>
   * The list of trips for the requested vehicle.
   * </pre>
   *
   * <code>repeated .maps.fleetengine.v1.Trip trips = 1;</code>
   */
  google.maps.fleetengine.v1.TripOrBuilder getTripsOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * Pass this token in the SearchTripsRequest to continue to
   * list results. If all results have been returned, this field is an empty
   * string or not present in the response.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   *
   * @return The nextPageToken.
   */
  java.lang.String getNextPageToken();
  /**
   *
   *
   * <pre>
   * Pass this token in the SearchTripsRequest to continue to
   * list results. If all results have been returned, this field is an empty
   * string or not present in the response.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   *
   * @return The bytes for nextPageToken.
   */
  com.google.protobuf.ByteString getNextPageTokenBytes();
}