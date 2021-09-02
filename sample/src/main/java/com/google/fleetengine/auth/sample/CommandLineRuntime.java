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

package com.google.fleetengine.auth.sample;

import static io.grpc.Status.PERMISSION_DENIED;

import com.google.fleetengine.auth.sample.validation.SampleScriptException;
import com.google.fleetengine.auth.sample.validation.SampleScriptRuntime;
import com.google.fleetengine.auth.sample.validation.ThrowingRunnable;
import io.grpc.StatusRuntimeException;

/**
 * Create runtime for a command line application.
 *
 * <p>Success and Failure messages are displayed within the terminal.
 */
final class CommandLineRuntime implements SampleScriptRuntime {
  private static final String EXPECTED_PERMISSION_DENIED_MESSAGE =
      "Expected StatusRuntimeException with code PERMISSION_DENIED.";
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\033[0;33m";

  /** Create runtime. */
  public static SampleScriptRuntime create() {
    return new CommandLineRuntime();
  }

  private CommandLineRuntime() {}

  /**
   * Runs a given command and displays whether or not it was successful. A failure is communicated
   * by the command throwing an Exception.
   *
   * @param commandName displayed immediately before the {@code command} is run
   * @param command command to run
   * @throws Throwable signals that the {@code command} failed
   */
  @Override
  public void runCommand(String commandName, ThrowingRunnable command) throws Throwable {
    System.out.printf("%s... ", commandName);
    try {
      command.run();
    } catch (Exception exception) {
      System.out.printf("%sFAIL!%s\n", ANSI_RED, ANSI_RESET);
      throw exception;
    }
    System.out.printf("%sSUCCESS%s\n", ANSI_GREEN, ANSI_RESET);
  }

  /** {@inheritDoc} */
  @Override
  public void expectPermissionDenied(String commandName, ThrowingRunnable command)
      throws Throwable {
    runCommand(
        commandName,
        () -> {
          try {
            command.run();
          } catch (com.google.api.gax.rpc.PermissionDeniedException e) {
            if (e.getCause() instanceof StatusRuntimeException) {
              checkException((StatusRuntimeException) e.getCause());
            }
          } catch (StatusRuntimeException e) {
            checkException(e);
          }
        });
  }

  private void checkException(StatusRuntimeException cause) throws Throwable {
    if (!PERMISSION_DENIED.getCode().equals(cause.getStatus().getCode())) {
      throw new SampleScriptException(EXPECTED_PERMISSION_DENIED_MESSAGE, cause);
    }
  }

  /** Print run script message. */
  public static void printRunScriptMessage(String scriptName) {
    System.out.printf("\nRunning %s token validation script...\n", scriptName);
  }

  /** Print message that the script was skipped over. */
  static void printSkipScriptMessage(String scriptName) {
    System.out.printf(
        "\n%sSkipping %s token validation script.%s\n", ANSI_YELLOW, scriptName, ANSI_RESET);
  }
}
