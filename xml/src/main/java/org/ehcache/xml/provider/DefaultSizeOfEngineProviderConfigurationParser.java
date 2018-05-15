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

import org.ehcache.impl.config.store.heap.DefaultSizeOfEngineProviderConfiguration;
import org.ehcache.xml.model.ConfigType;
import org.ehcache.xml.model.MemoryType;
import org.ehcache.xml.model.MemoryUnit;
import org.ehcache.xml.model.SizeOfEngineLimits;
import org.ehcache.xml.model.SizeofType;

import java.math.BigInteger;

public class DefaultSizeOfEngineProviderConfigurationParser
  extends SimpleCoreServiceCreationConfigurationParser<SizeofType, DefaultSizeOfEngineProviderConfiguration> {

  public DefaultSizeOfEngineProviderConfigurationParser() {
    super(ConfigType::getHeapStore,
      config -> {
        SizeOfEngineLimits sizeOfEngineLimits = new SizeOfEngineLimits(config);
        return new DefaultSizeOfEngineProviderConfiguration(sizeOfEngineLimits.getMaxObjectSize(),
          sizeOfEngineLimits.getUnit(), sizeOfEngineLimits.getMaxObjectGraphSize());
      },
      DefaultSizeOfEngineProviderConfiguration.class,
      (configType, config) -> {
        SizeofType sizeofType = new SizeofType();
        SizeofType.MaxObjectGraphSize maxObjectGraphSize = new SizeofType.MaxObjectGraphSize();
        maxObjectGraphSize.setValue(BigInteger.valueOf(config.getMaxObjectGraphSize()));
        sizeofType.setMaxObjectGraphSize(maxObjectGraphSize);
        MemoryType maxObjectSize = new MemoryType();
        maxObjectSize.setValue(BigInteger.valueOf(config.getMaxObjectSize()));
        maxObjectSize.setUnit(MemoryUnit.fromValue(config.getUnit().toString()));
        sizeofType.setMaxObjectSize(maxObjectSize);
        configType.setHeapStore(sizeofType);
      }
    );
  }
}
