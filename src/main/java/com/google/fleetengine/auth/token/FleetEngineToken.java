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

package com.google.fleetengine.auth.token;

import com.google.auto.value.AutoValue;
import com.google.fleetengine.auth.token.factory.signer.util.CommonConstants;
import java.util.Date;
import javax.annotation.Nullable;

/**
 * Provides the authorization information needed to establish communication with Fleet Engine
 * servers.
 */
@AutoValue
public abstract class FleetEngineToken {
  /** Token creation timestamp. */
  public abstract Date creationTimestamp();

  /** Token expiration timestamp. */
  public abstract Date expirationTimestamp();

  /** Audience (aud claim) of the JWT. */
  public abstract String audience();

  /** Signed and base64 encoded JWT. */
  @Nullable
  public abstract String jwt();

  /** Scope of the authorization token. */
  public abstract FleetEngineTokenType tokenType();

  /** Custom authorization claims written to JWT. */
  public abstract FleetEngineTokenClaims authorizationClaims();

  /** Provides a builder for {@link FleetEngineToken}. */
  public static Builder builder() {
    return new AutoValue_FleetEngineToken.Builder()
        .setAudience(CommonConstants.DEFAULT_JWT_AUDIENCE);
  }

  public abstract FleetEngineToken.Builder toBuilder();

  /** Builder class for FleetEngineToken. */
  @AutoValue.Builder
  public abstract static class Builder {

    /** Setter for token creation timestamp. */
    public abstract Builder setCreationTimestamp(Date creationTimestamp);

    /** Setter for token expiration timestamp. */
    public abstract Builder setExpirationTimestamp(Date expirationTimestamp);

    /** Setter for a signed JWT to be associated with the token object. */
    public abstract Builder setJwt(String token);

    /** Setter for the token type defining the scope of the token. */
    public abstract Builder setTokenType(FleetEngineTokenType type);

    /** Setter for token audience (aud claim). */
    public abstract Builder setAudience(String audience);

    /** Sets custom authorization claims written to JWT. */
    public abstract Builder setAuthorizationClaims(FleetEngineTokenClaims claims);

    /** Builds the token object. */
    public abstract FleetEngineToken build();
  }
}
