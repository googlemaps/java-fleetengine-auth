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
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.factory.signer.Signer;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides valid Fleet Engine tokens with a caching mechanism for tokens with wildcard claims.
 *
 * <p>Implements a base token rotation system for cached tokens.
 *
 * <p>Fleet Engine tokens that do not have wildcard claims are not cached for the following reasons:
 *
 * <ol>
 *   <li>Tokens with non-wildcard claims have extra key information attached to them, eg. a trip id.
 *       This requires a complete cache strategy including key expirations. The implementation can
 *       vary dramatically depending on a provider's use case.
 *   <li>In the typical case, tokens with non-wildcard claims are minted for non-trusted
 *       applications that can't sign tokens directly eg. a driver application running on a mobile
 *       device. In these cases, the non-trusted application can cache the token locally.
 * </ol>
 */
public class NaiveAuthStateManager implements FleetEngineAuthTokenStateManager {

  /** Validates whether a token has expired. */
  private final FleetEngineTokenExpiryValidator tokenExpiryValidator;

  private final ConcurrentHashMap<FleetEngineTokenType, FleetEngineToken> cachedWildcardTokens;

  /** Constructor. */
  public NaiveAuthStateManager() {
    this(FleetEngineTokenExpiryValidator.getInstance());
  }

  /** Constructor for testing. */
  @VisibleForTesting
  NaiveAuthStateManager(FleetEngineTokenExpiryValidator tokenExpiryValidator) {
    this.tokenExpiryValidator = tokenExpiryValidator;
    this.cachedWildcardTokens = new ConcurrentHashMap<>(3);
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken signToken(Signer signer, FleetEngineToken token)
      throws SigningTokenException {

    if (!token.authorizationClaims().isWildcard()) {
      // Always sign tokens with claims that are not a wildcard.
      return signer.sign(token);
    }

    FleetEngineToken cachedToken = getNonExpiredCachedToken(token);
    if (cachedToken != null) {
      return cachedToken;
    }

    synchronized (cachedWildcardTokens) {
      // The token may have been refreshed by another thread.
      cachedToken = getNonExpiredCachedToken(token);
      if (cachedToken != null) {
        return cachedToken;
      }

      // The cached token is either null or expired, in either case, sign the token and cache it.
      FleetEngineToken signedToken = signer.sign(token);
      cachedWildcardTokens.put(signedToken.tokenType(), signedToken);
      return signedToken;
    }
  }

  private FleetEngineToken getNonExpiredCachedToken(FleetEngineToken token) {
    FleetEngineToken cachedToken = cachedWildcardTokens.get(token.tokenType());
    if (cachedToken != null
        && !tokenExpiryValidator.isTokenExpired(cachedToken, EXPIRATION_WINDOW_DURATION)) {
      // Cached token exists and is not expired.
      return cachedToken;
    }
    return null;
  }
}
