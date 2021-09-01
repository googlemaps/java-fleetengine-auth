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

import com.google.common.collect.ImmutableMap;
import com.google.fleetengine.auth.token.FleetEngineTokenClaims;

public class EmptyFleetEngineTokenClaims implements FleetEngineTokenClaims {
  public static final EmptyFleetEngineTokenClaims INSTANCE = new EmptyFleetEngineTokenClaims();

  private static final ImmutableMap<String, String> MAP = ImmutableMap.of();

  private EmptyFleetEngineTokenClaims() {}

  @Override
  public ImmutableMap<String, String> toMap() {
    return MAP;
  }

  @Override
  public boolean isWildcard() {
    return false;
  }
}
