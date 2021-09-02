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

class UnitTestSampleScriptRuntime implements SampleScriptRuntime {
  private boolean expectPermissionDeniedCalled = false;

  public boolean wasExpectPermissionDeniedCalled() {
    return expectPermissionDeniedCalled;
  }

  @Override
  public void runCommand(String commandName, ThrowingRunnable command) throws Throwable {
    command.run();
  }

  @Override
  public void expectPermissionDenied(String commandName, ThrowingRunnable command)
      throws Throwable {
    expectPermissionDeniedCalled = true;
    command.run();
  }
}
