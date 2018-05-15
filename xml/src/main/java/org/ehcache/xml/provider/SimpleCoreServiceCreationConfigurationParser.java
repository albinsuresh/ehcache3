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

package org.ehcache.xml.provider;

import org.ehcache.config.Configuration;
import org.ehcache.config.builders.ConfigurationBuilder;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.spi.service.ServiceCreationConfiguration;
import org.ehcache.xml.CoreServiceCreationConfigurationParser;
import org.ehcache.xml.model.ConfigType;

import java.util.function.BiConsumer;
import java.util.function.Function;

class SimpleCoreServiceCreationConfigurationParser<T, U extends ServiceCreationConfiguration> implements CoreServiceCreationConfigurationParser {

  private final Function<ConfigType, T> extractor;
  private final Parser<T> parser;

  private final Class<U> configClass;
  private final BiConsumer<ConfigType, U> serviceConfigMarshaller;

  SimpleCoreServiceCreationConfigurationParser(Function<ConfigType, T> extractor, Function<T, ServiceCreationConfiguration<?>> parser,
                                               Class<U> clazz, BiConsumer<ConfigType, U> serviceConfigMarshaller) {
    this(extractor, (c, l) -> parser.apply(c), clazz, serviceConfigMarshaller);
  }

  SimpleCoreServiceCreationConfigurationParser(Function<ConfigType, T> extractor, Parser<T> parser,
                                               Class<U> clazz, BiConsumer<ConfigType, U> serviceConfigMarshaller) {
    this.extractor = extractor;
    this.parser = parser;
    this.configClass = clazz;
    this.serviceConfigMarshaller = serviceConfigMarshaller;
  }

  @Override
  public final ConfigurationBuilder parseServiceCreationConfiguration(ConfigType root, ClassLoader classLoader, ConfigurationBuilder builder) throws ClassNotFoundException {
    T config = extractor.apply(root);
    if (config == null) {
      return builder;
    } else {
      return builder.addService(parser.parse(config, classLoader));
    }
  }

  @Override
  public void unparseServiceCreationConfiguration(ConfigType configType, Configuration configuration) {
    U config = ServiceUtils.findSingletonAmongst(configClass, configuration.getServiceCreationConfigurations());
    if (config != null) {
      serviceConfigMarshaller.accept(configType, config);
    }
  }

  @FunctionalInterface
  interface Parser<T> {

    ServiceCreationConfiguration<?> parse(T t, ClassLoader classLoader) throws ClassNotFoundException;
  }
}
