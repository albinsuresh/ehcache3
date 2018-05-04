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
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.ehcache.xml.CoreServiceCreationConfigurationParser;
import org.ehcache.xml.model.ConfigType;
import org.ehcache.xml.model.PersistenceType;

import java.io.File;

public class CacheManagerPersistenceConfigurationParser implements CoreServiceCreationConfigurationParser {

  @Override
  public ConfigurationBuilder parseServiceCreationConfiguration(ConfigType root, ClassLoader classLoader, ConfigurationBuilder builder) {
    PersistenceType persistenceType = root.getPersistence();
    if (persistenceType != null) {
      builder = builder.addService(new CacheManagerPersistenceConfiguration(new File(persistenceType.getDirectory())));
    }

    return builder;
  }
}
