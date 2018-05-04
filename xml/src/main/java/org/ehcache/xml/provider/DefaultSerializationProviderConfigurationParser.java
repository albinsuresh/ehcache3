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

import org.ehcache.config.builders.ConfigurationBuilder;
import org.ehcache.xml.CoreServiceCreationConfigurationParser;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.ehcache.xml.model.ConfigType;
import org.ehcache.xml.model.SerializerType;

import static org.ehcache.xml.XmlConfiguration.getClassForName;

public class DefaultSerializationProviderConfigurationParser implements CoreServiceCreationConfigurationParser {

  @Override
  public ConfigurationBuilder parseServiceCreationConfiguration(ConfigType root, ClassLoader classLoader, ConfigurationBuilder builder) {
    SerializerType serializerType = root.getDefaultSerializers();
    if (serializerType != null) {
      org.ehcache.impl.config.serializer.DefaultSerializationProviderConfiguration configuration = new org.ehcache.impl.config.serializer.DefaultSerializationProviderConfiguration();

      for (SerializerType.Serializer serializer : serializerType.getSerializer()) {
        try {
          configuration.addSerializerFor(getClassForName(serializer.getType(), classLoader), (Class) getClassForName(serializer.getValue(), classLoader));
        } catch (ClassNotFoundException e) {
          throw new XmlConfigurationException(e);
        }
      }
      builder = builder.addService(configuration);
    }

    return builder;
  }
}
