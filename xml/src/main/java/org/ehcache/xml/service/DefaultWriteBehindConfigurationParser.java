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

import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder.BatchedWriteBehindConfigurationBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder.UnBatchedWriteBehindConfigurationBuilder;
import org.ehcache.impl.config.loaderwriter.writebehind.DefaultWriteBehindConfiguration;
import org.ehcache.spi.loaderwriter.WriteBehindConfiguration;
import org.ehcache.xml.model.BaseCacheType;
import org.ehcache.xml.model.CacheLoaderWriterType;
import org.ehcache.xml.model.CacheTemplate;
import org.ehcache.xml.model.TimeType;
import org.ehcache.xml.model.TimeUnit;

import java.math.BigInteger;

import static java.util.Optional.ofNullable;
import static org.ehcache.config.builders.WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration;
import static org.ehcache.xml.XmlModel.convertToJUCTimeUnit;

public class DefaultWriteBehindConfigurationParser
  extends SimpleCoreServiceConfigurationParser<CacheLoaderWriterType.WriteBehind, CacheLoaderWriterType, WriteBehindConfiguration> {

  public DefaultWriteBehindConfigurationParser() {
    super(WriteBehindConfiguration.class,
      CacheTemplate::writeBehind,
      config -> ofNullable(config.getBatching()).<WriteBehindConfigurationBuilder>map(batching -> {
        BatchedWriteBehindConfigurationBuilder batchedBuilder = newBatchedWriteBehindConfiguration(batching.getMaxWriteDelay().getValue().longValue(), convertToJUCTimeUnit(batching.getMaxWriteDelay().getUnit()), batching.getBatchSize().intValue());
        if (batching.isCoalesce()) {
          batchedBuilder = batchedBuilder.enableCoalescing();
        }
        return batchedBuilder;
      }).orElseGet(UnBatchedWriteBehindConfigurationBuilder::newUnBatchedWriteBehindConfiguration).useThreadPool(config.getThreadPool())
        .concurrencyLevel(config.getConcurrency().intValue())
        .queueSize(config.getSize().intValue()).build(),
      BaseCacheType::getLoaderWriter, BaseCacheType::setLoaderWriter,
      config -> {
        CacheLoaderWriterType loaderWriter = new CacheLoaderWriterType();
        CacheLoaderWriterType.WriteBehind writeBehind = new CacheLoaderWriterType.WriteBehind();
        writeBehind.setThreadPool(config.getThreadPoolAlias());
        writeBehind.setConcurrency(BigInteger.valueOf(config.getConcurrency()));
        writeBehind.setSize(BigInteger.valueOf(config.getMaxQueueSize()));

        WriteBehindConfiguration.BatchingConfiguration batchingConfiguration = config.getBatchingConfiguration();
        if (batchingConfiguration == null) {
          writeBehind.setNonBatching(null);
        } else {
          CacheLoaderWriterType.WriteBehind.Batching batching = new CacheLoaderWriterType.WriteBehind.Batching();
          batching.setBatchSize(BigInteger.valueOf(batchingConfiguration.getBatchSize()));
          batching.setCoalesce(batchingConfiguration.isCoalescing());
          TimeType timeType = new TimeType();
          timeType.setValue(BigInteger.valueOf(batchingConfiguration.getMaxDelay()));
          timeType.setUnit(TimeUnit.fromValue(batchingConfiguration.getMaxDelayUnit().name().toLowerCase()));
          batching.setMaxWriteDelay(timeType);
          writeBehind.setBatching(batching);
        }
        loaderWriter.setWriteBehind(writeBehind);
        return loaderWriter;
      },
      (existing, additional) -> {
        existing.setWriteBehind(additional.getWriteBehind());
        return existing;
      });
  }
}
