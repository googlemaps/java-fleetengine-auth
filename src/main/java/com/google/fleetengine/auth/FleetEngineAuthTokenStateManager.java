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

import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.factory.signer.Signer;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import java.time.Duration;

/**
 * Provides non-expired Fleet Engine tokens.
 *
 * <p>Tokens can be freshly minted each time or reused.
 *
 * <p>Tokens are guaranteed to not expire for a duration of {@link
 * FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION} (currently 5 minutes.)
 */
public interface FleetEngineAuthTokenStateManager {

  /**
   * Tokens returned from any of the {@code findOrCreateToken} methods are guaranteed to be valid
   * for the given expiration window (currently 5 minutes.)
   */
  Duration EXPIRATION_WINDOW_DURATION = Duration.ofMinutes(5);

  /**
   * Either signs {@code token} or reuses an existing non-expired token.
   *
   * @param signer signer used to sign the token
   * @param token unsigned token
   * @return Signed Fleet Engine token that is guaranteed not to expire for the next {@link
   *     FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION}
   */
  FleetEngineToken signToken(Signer signer, FleetEngineToken token) throws SigningTokenException;
}
