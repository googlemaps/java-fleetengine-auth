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

package com.google.fleetengine.auth.sample.validation;

import java.util.UUID;

/** Common utility methods used across scripts. */
final class ScriptUtils {

  private ScriptUtils() {}

  /** Get provider name. This is set as the parent of requests. */
  public static String getProviderName(String providerId) {
    return String.format("providers/%s", providerId);
  }

  /** Generate a random vehicle id. */
  public static String generateRandomVehicleId() {
    return String.format("vehicle-%s", UUID.randomUUID());
  }

  /** Generate a random trip id. */
  public static String generateRandomTripId() {
    return String.format("trip-%s", UUID.randomUUID());
  }

  public static String generateRandomTaskId() {
    return String.format("task-%s", UUID.randomUUID());
  }

  public static String generateRandomDeliveryVehicleId() {
    return String.format("delivery-vehicle-%s", UUID.randomUUID());
  }

  public static String generateRandomTrackingId() {
    return String.format("tracking-%s", UUID.randomUUID());
  }
}
