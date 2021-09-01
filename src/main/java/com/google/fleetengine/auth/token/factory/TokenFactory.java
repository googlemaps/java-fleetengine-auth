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

package com.google.fleetengine.auth.token.factory;

import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenClaims;
import com.google.fleetengine.auth.token.TripClaims;
import com.google.fleetengine.auth.token.VehicleClaims;

/**
 * An interface for a factory providing valid JWT (JSON Web Tokens) to be used on communications
 * with Fleet Engine server.
 */
public interface TokenFactory {

  /**
   * Creates tokens for the {@link com.google.fleetengine.auth.token.FleetEngineTokenType#SERVER}
   * role.
   */
  FleetEngineToken createServerToken();

  /**
   * Creates tokens for the {@link com.google.fleetengine.auth.token.FleetEngineTokenType#DRIVER}
   * role.
   */
  FleetEngineToken createDriverToken(VehicleClaims claims);

  /**
   * Creates tokens for the {@link com.google.fleetengine.auth.token.FleetEngineTokenType#CONSUMER}
   * role.
   */
  FleetEngineToken createConsumerToken(TripClaims claims);

  /**
   * Creates tokens for tokens of type {@link
   * com.google.fleetengine.auth.token.FleetEngineTokenType#CUSTOM} and can be associated to any
   * Fleet Engine role.
   */
  FleetEngineToken createCustomToken(FleetEngineTokenClaims claims);
}
