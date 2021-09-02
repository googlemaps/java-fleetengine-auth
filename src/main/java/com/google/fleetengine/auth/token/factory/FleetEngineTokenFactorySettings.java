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

import com.google.auto.value.AutoValue;
import com.google.fleetengine.auth.token.factory.signer.util.CommonConstants;
import javax.annotation.Nullable;

/** Settings for {@link FleetEngineTokenFactory}. */
@AutoValue
public abstract class FleetEngineTokenFactorySettings {
  /** Audience (aud claim) of JWTs. */
  @Nullable
  public abstract String audience();

  public static Builder builder() {
    return new AutoValue_FleetEngineTokenFactorySettings.Builder()
        .setAudience(CommonConstants.DEFAULT_JWT_AUDIENCE);
  }

  /** Builder class for FleetEngineTokenFactorySettings. */
  @AutoValue.Builder
  public abstract static class Builder {
    /** Sets the audience (aud claim) of new tokens. */
    public abstract Builder setAudience(String audience);

    /** Builds {@link FleetEngineTokenFactorySettings}. */
    public abstract FleetEngineTokenFactorySettings build();
  }
}
