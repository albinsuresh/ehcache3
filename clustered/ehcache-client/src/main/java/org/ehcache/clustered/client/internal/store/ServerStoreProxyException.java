/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.client.internal.store;

public class ServerStoreProxyException extends RuntimeException {

  private static final long serialVersionUID = -3451273597124838171L;

  /**
   * Creates a new exception wrapping the {@link Throwable cause} passed in.
   *
   * @param cause the cause of this exception
   */
  public ServerStoreProxyException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a {@code ServerStoreProxyException} with the provided message.
   *
   * @param message information about the exception
   */
  public ServerStoreProxyException(String message) {
    super(message);
  }
}
