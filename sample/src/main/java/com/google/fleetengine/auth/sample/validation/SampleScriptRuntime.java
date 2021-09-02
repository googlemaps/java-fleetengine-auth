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

/**
 * Sample scripts give feedback to the runtime such as progress and whether or not it succeeded. The
 * feedback will be presented differently depending on the runtime environment. For example, if the
 * runtime is a command line application, then the feedback is displayed in the terminal. For an
 * integration test though, an assert would be used.
 */
public interface SampleScriptRuntime {

  /**
   * Run a command that can throw an exception.
   *
   * @param commandName human readable name that describes the command
   * @param command runnable command
   * @throws Throwable signals that the command failed
   */
  void runCommand(String commandName, ThrowingRunnable command) throws Throwable;

  /**
   * Expects that {@code command} throws {@link io.grpc.StatusRuntimeException} with code {@code
   * io.grpc.Status.PERMISSION_DENIED}. If that exception is thrown but with a different code, then
   * {@link SampleScriptException} is thrown.
   *
   * @param commandName human readable name that describes the command
   * @param command runnable command
   * @throws Throwable signals that the command failed
   */
  void expectPermissionDenied(String commandName, ThrowingRunnable command) throws Throwable;
}
