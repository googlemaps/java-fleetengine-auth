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

package com.google.fleetengine.auth.token.factory.signer.util;

import com.google.common.io.BaseEncoding;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Logger;

/** Utility class to generate RSA Private Keys. */
public final class RSAPrivateKeyUtils {

  public static final String KEY_ALGORITHM = "RSA";

  private RSAPrivateKeyUtils() {}

  private static final Logger logger = Logger.getLogger(RSAPrivateKeyUtils.class.getName());

  private static final String NEW_LINE = "\n";
  private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
  private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

  /**
   * Generates a private key object given a private key string, using the RSA-256 algorithm.
   *
   * @param privateKey private key string
   * @throws InvalidKeySpecException if the provided private key is invalid.
   * @throws IllegalStateException if no provider accepts a {@link KeyFactory} implementation for
   *     the provided algorithm.
   */
  public static RSAPrivateKey getPrivateKey(String privateKey) throws InvalidKeySpecException {
    String key = cleanUpKeyString(privateKey);

    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(key));
    KeyFactory keyFactory;

    try {
      keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      logger.warning(String.format("Algorithm [%s] is not supported.", KEY_ALGORITHM));
      throw new IllegalStateException(e);
    }

    return (RSAPrivateKey) keyFactory.generatePrivate(spec);
  }

  /**
   * Replaces common unnecessary characters sequences from private key string.
   *
   * @param privateKey private key string
   */
  private static final String cleanUpKeyString(String privateKey) {
    return privateKey
        .replace(BEGIN_PRIVATE_KEY, "")
        .replace(END_PRIVATE_KEY, "")
        .replace(NEW_LINE, "");
  }
}
