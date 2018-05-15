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

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.xml.CoreServiceConfigurationParser;
import org.ehcache.xml.model.CacheTemplate;
import org.ehcache.xml.model.CacheType;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

class SimpleCoreServiceConfigurationParser<T, U extends ServiceConfiguration> implements CoreServiceConfigurationParser {

  private final Function<CacheTemplate, T> extractor;
  private final Parser<T> parser;

  private final Class<U> configClass;
  private final BiConsumer<CacheType, U> configMarshaller;

  SimpleCoreServiceConfigurationParser(Function<CacheTemplate, T> extractor, Function<T, ServiceConfiguration<?>> parser,
                                       Class<U> clazz, BiConsumer<CacheType, U> configMarshaller) {
    this(extractor, (config, loader) -> parser.apply(config), clazz, configMarshaller);
  }

  SimpleCoreServiceConfigurationParser(Function<CacheTemplate, T> extractor, Parser<T> parser,
                                       Class<U> clazz, BiConsumer<CacheType, U> configMarshaller) {
    this.extractor = extractor;
    this.parser = parser;
    this.configClass = clazz;
    this.configMarshaller = configMarshaller;
  }

  @Override
  public final <K, V> CacheConfigurationBuilder<K, V> parseServiceConfiguration(CacheTemplate cacheDefinition, ClassLoader cacheClassLoader, CacheConfigurationBuilder<K, V> cacheBuilder) throws ClassNotFoundException {
    T config = extractor.apply(cacheDefinition);
    if (config != null) {
      ServiceConfiguration<?> configuration = parser.parse(config, cacheClassLoader);
      if (configuration != null) {
        return cacheBuilder.add(configuration);
      }
    }
    return cacheBuilder;
  }

  @Override
  public void unparseServiceConfiguration(CacheType cacheType, CacheConfiguration<?, ?> cacheConfiguration) {
    U serviceConfig = ServiceUtils.findSingletonAmongst(configClass, cacheConfiguration.getServiceConfigurations());
    if (serviceConfig != null) {
      configMarshaller.accept(cacheType, serviceConfig);
    }
  }

  @FunctionalInterface
  interface Parser<T> {

    ServiceConfiguration<?> parse(T t, ClassLoader classLoader) throws ClassNotFoundException;
  }
}
