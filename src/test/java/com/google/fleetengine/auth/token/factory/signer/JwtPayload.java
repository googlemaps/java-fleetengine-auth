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

package com.google.fleetengine.auth.token.factory.signer;

import com.google.gson.annotations.SerializedName;

/** A Fleet Engine compatible JWT payload with necessary custom claims. */
class JwtPayload {
  /** Identifies the principal that issued the JWT. */
  @SerializedName("iss")
  public String issuer;

  /** Identifies the recipients that the JWT is intended for. */
  @SerializedName("aud")
  public String audience;

  /** Identifies the principal that is the subject of the JWT. */
  @SerializedName("sub")
  public String subject;

  /** Identifies the time at which the JWT was issued. */
  @SerializedName("iat")
  public Long issuedAt;

  /** Identifies the time at which JWT is expired. */
  @SerializedName("exp")
  public Long expiredAt;

  /** Identifies the Fleet Engine IDs that the JWT token should be limited. */
  public Authorization authorization = new Authorization();

  /**
   * Represents the authorization custom claim that Fleet Engines use to limit the scope of a token.
   */
  static class Authorization {
    static final String CLAIM_KEY_1 = "key_1";
    static final String CLAIM_KEY_2 = "key_2";

    @SerializedName(CLAIM_KEY_1)
    public String numberOne;

    @SerializedName(CLAIM_KEY_2)
    public String numberTwo;
  }
}
