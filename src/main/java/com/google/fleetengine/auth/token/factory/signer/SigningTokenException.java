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

/**
 * Signals that an exception occurred while signing a Fleet Engine token.
 *
 * @see Signer
 */
public class SigningTokenException extends Exception {
  /**
   * Constructs a {@code SigningTokenException} with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the {@link
   *     #getMessage()} method)
   */
  public SigningTokenException(String message) {
    this(message, null);
  }

  /**
   * Constructs a {@code SigningTokenException} with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the {@link
   *     #getMessage()} method)
   * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method).
   *     (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public SigningTokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
