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
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.xml.CoreServiceConfigurationParser;
import org.ehcache.xml.model.CacheLoaderWriterType;
import org.ehcache.xml.model.CacheTemplate;

import static org.ehcache.xml.XmlModel.convertToJUCTimeUnit;

public class DefaultWriteBehindConfigurationParser<K, V> implements CoreServiceConfigurationParser<K, V> {

  @Override
  public CacheConfigurationBuilder<K, V> parseServiceConfiguration(CacheTemplate cacheDefinition, ClassLoader cacheClassLoader,
                                                                   CacheConfigurationBuilder<K, V> cacheBuilder) {
    if(cacheDefinition.writeBehind() != null) {
      CacheLoaderWriterType.WriteBehind writeBehind = cacheDefinition.writeBehind();
      WriteBehindConfigurationBuilder writeBehindConfigurationBuilder;
      if (writeBehind.getBatching() == null) {
        writeBehindConfigurationBuilder = WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration();
      } else {
        CacheLoaderWriterType.WriteBehind.Batching batching = writeBehind.getBatching();
        writeBehindConfigurationBuilder = WriteBehindConfigurationBuilder
          .newBatchedWriteBehindConfiguration(
            batching.getMaxWriteDelay().getValue().longValue(),
            convertToJUCTimeUnit(batching.getMaxWriteDelay().getUnit()),
            batching.getBatchSize().intValue());
        if (batching.isCoalesce()) {
          writeBehindConfigurationBuilder = ((WriteBehindConfigurationBuilder.BatchedWriteBehindConfigurationBuilder) writeBehindConfigurationBuilder).enableCoalescing();
        }
      }
      cacheBuilder = cacheBuilder.add(writeBehindConfigurationBuilder.useThreadPool(writeBehind.getThreadPool())
        .concurrencyLevel(writeBehind.getConcurrency().intValue())
        .queueSize(writeBehind.getSize().intValue()).build());
    }

    return cacheBuilder;
  }
}
