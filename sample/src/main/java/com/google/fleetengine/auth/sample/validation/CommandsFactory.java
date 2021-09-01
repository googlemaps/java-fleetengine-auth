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

import com.google.fleetengine.auth.client.FleetEngineTokenProvider;
import java.io.IOException;

/** Creates command classes. */
public interface CommandsFactory {

  /** Creates trips command class. */
  TripCommands createTripCommands(
      String fleetEngineAddress, String providerId, FleetEngineTokenProvider tokenProvider)
      throws IOException;

  /** Creates vehicles command class. */
  VehicleCommands createVehicleCommands(
      String fleetEngineAddress, String providerId, FleetEngineTokenProvider tokenProvider)
      throws IOException;
}
