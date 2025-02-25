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

package org.ehcache.impl.internal.store.shared.composites;

import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.store.tiering.CachingTier.InvalidationListener;
import org.ehcache.impl.internal.store.shared.store.StorePartition;

import java.util.Map;

public class CompositeInvalidationListener implements InvalidationListener<CompositeValue<?>, CompositeValue<?>> {

  private final Map<Integer, InvalidationListener<?, ?>> invalidationListenerMap;

  public CompositeInvalidationListener(Map<Integer, InvalidationListener<?, ?>> invalidationListenerMap) {
    this.invalidationListenerMap = invalidationListenerMap;
  }

  public Map<Integer, InvalidationListener<?, ?>> getComposites() {
    return invalidationListenerMap;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void onInvalidation(CompositeValue<?> key, Store.ValueHolder<CompositeValue<?>> valueHolder) {
    InvalidationListener<?, ?> listener = invalidationListenerMap.get(key.getStoreId());
    if (listener != null) {
      ((InvalidationListener) listener).onInvalidation(key.getValue(), new StorePartition.DecodedValueHolder(valueHolder));
    }
  }
}
