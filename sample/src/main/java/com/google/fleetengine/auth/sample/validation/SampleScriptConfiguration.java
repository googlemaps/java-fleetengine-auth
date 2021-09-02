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

import com.google.auto.value.AutoValue;
import com.google.fleetengine.auth.AuthTokenMinter;

/** Configuration values needed to run a sample script. */
@AutoValue
public abstract class SampleScriptConfiguration {
  /** Get the fleet engine address. */
  public abstract String getFleetEngineAddress();

  /** Get the provider id. */
  public abstract String getProviderId();

  /** Get the {@link AuthTokenMinter} to use with sample scripts. */
  public abstract AuthTokenMinter getMinter();

  /** Create SampleScriptConfiguration builder */
  public static SampleScriptConfiguration.Builder builder() {
    return new com.google.fleetengine.auth.sample.validation.AutoValue_SampleScriptConfiguration
        .Builder();
  }

  /** SampleScriptConfiguration builder */
  @AutoValue.Builder
  public abstract static class Builder {
    /**
     * Set the fleet engine address in the form of host:port.
     *
     * <p>For example: "fleetengine.googleapis.com:443"
     */
    public abstract Builder setFleetEngineAddress(String address);

    /** Sets the provider id. */
    public abstract Builder setProviderId(String providerName);

    /** Set {@link AuthTokenMinter} use with a sample script. */
    public abstract Builder setMinter(AuthTokenMinter minter);

    /** Build {@link SampleScriptConfiguration}. */
    public abstract SampleScriptConfiguration build();
  }
}
