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

package org.ehcache.spi.persistence;

import java.util.Map;
import java.util.Set;

/**
 * A {@code Map} like structure that can hold key value mappings.
 *
 * @param <K> type of Keys
 * @param <V> type of Values
 */
public interface StateHolder<K, V> {

  /**
   * If the specified key is not already associated with a value (or is mapped
   * to {@code null}) associates it with the given value and returns
   * {@code null}, else returns the current value.
   *
   * @param key a key
   * @param value a value
   * @return the previous value associated with the specified key, or
   *         {@code null} if there was no mapping for the key.
   */
  V putIfAbsent(K key, V value);

  /**
   * Retrieves the value mapped to the given {@code key}
   *
   * @param key a key
   * @return  the value mapped to the key
   */
  V get(K key);

  /**
   * Removes the entry for a key only if currently mapped to a given value.
   *
   * @param key a key with which the specified value is associated
   * @param value value expected to be associated with the specified key
   * @return {@code true} if the value was removed
   */
  boolean remove(K key, V value);

  /**
   * Retrieves all the entries in the {@code StateHolder} as a {@code Set} of {@code Map.Entry} instances.
   *
   * @return the set of this {@code StateHolder} mappings
   */
  Set<Map.Entry<K, V>> entrySet();

}
