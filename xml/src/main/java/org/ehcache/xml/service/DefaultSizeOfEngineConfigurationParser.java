/*
 * Copyright Terracotta, Inc.
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

package org.ehcache.xml.service;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.impl.config.store.heap.DefaultSizeOfEngineConfiguration;
import org.ehcache.xml.CoreServiceConfigurationParser;
import org.ehcache.xml.model.CacheTemplate;

public class DefaultSizeOfEngineConfigurationParser<K, V> implements CoreServiceConfigurationParser<K, V> {

  @Override
  public CacheConfigurationBuilder<K, V> parseServiceConfiguration(CacheTemplate cacheDefinition, ClassLoader cacheClassLoader,
                                                                   CacheConfigurationBuilder<K, V> cacheBuilder) {
    if (cacheDefinition.heapStoreSettings() != null) {
      cacheBuilder = cacheBuilder.add(new DefaultSizeOfEngineConfiguration(cacheDefinition.heapStoreSettings().getMaxObjectSize(),
        cacheDefinition.heapStoreSettings().getUnit(), cacheDefinition.heapStoreSettings().getMaxObjectGraphSize()));
    }

    return cacheBuilder;
  }
}
