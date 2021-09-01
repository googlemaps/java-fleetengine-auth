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

package com.google.fleetengine.auth;

import com.google.common.annotations.VisibleForTesting;
import com.google.fleetengine.auth.token.FleetEngineToken;
import java.time.Clock;
import java.time.Duration;

/** Token expiration checker. Used for injection purposes. */
public class FleetEngineTokenExpiryValidator {

  /** Singleton for non-test cases. */
  private static final FleetEngineTokenExpiryValidator singleton =
      new FleetEngineTokenExpiryValidator(Clock.systemUTC());

  /* Used to get the current time. */
  private final Clock clock;

  /** Get singleton instance of the expiry validator. */
  public static FleetEngineTokenExpiryValidator getInstance() {
    return singleton;
  }

  /** Constructor. */
  @VisibleForTesting
  FleetEngineTokenExpiryValidator(Clock clock) {
    this.clock = clock;
  }

  /**
   * Verifies if the token will expire within a given window before the token's expiration
   * timestamp.
   *
   * <p>Put another way:
   *
   * <p>&nbsp;&nbsp;isTokenExpired = token.expirationTimestamp - expirationWindow < now
   *
   * @param token fleet engine token to validate
   * @param expirationWindow expiration window to check token expiry against
   */
  public boolean isTokenExpired(FleetEngineToken token, Duration expirationWindow) {
    return token
        .expirationTimestamp()
        .toInstant()
        .minus(expirationWindow)
        .isBefore(clock.instant());
  }
}
