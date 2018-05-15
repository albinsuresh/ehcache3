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

import org.ehcache.impl.config.executor.PooledExecutionServiceConfiguration;
import org.ehcache.xml.model.ConfigType;
import org.ehcache.xml.model.ThreadPoolsType;

import java.math.BigInteger;
import java.util.List;

public class PooledExecutionServiceConfigurationParser
  extends SimpleCoreServiceCreationConfigurationParser<ThreadPoolsType, PooledExecutionServiceConfiguration> {

  public PooledExecutionServiceConfigurationParser() {
    super(ConfigType::getThreadPools,
      config -> {
        PooledExecutionServiceConfiguration poolsConfiguration = new PooledExecutionServiceConfiguration();
        for (ThreadPoolsType.ThreadPool pool : config.getThreadPool()) {
          if (pool.isDefault()) {
            poolsConfiguration.addDefaultPool(pool.getAlias(), pool.getMinSize().intValue(), pool.getMaxSize().intValue());
          } else {
            poolsConfiguration.addPool(pool.getAlias(), pool.getMinSize().intValue(), pool.getMaxSize().intValue());
          }
        }
        return poolsConfiguration;
      },
      PooledExecutionServiceConfiguration.class,
      (configType, config) -> {
        ThreadPoolsType threadPoolsType = new ThreadPoolsType();
        List<ThreadPoolsType.ThreadPool> threadPools = threadPoolsType.getThreadPool();
        config.getPoolConfigurations().forEach((alias, poolConfig) -> {
          ThreadPoolsType.ThreadPool threadPool = new ThreadPoolsType.ThreadPool();
          threadPool.setAlias(alias);
          if (alias.equals(config.getDefaultPoolAlias())) {
            threadPool.setDefault(true);
          }
          threadPool.setMinSize(BigInteger.valueOf(poolConfig.minSize()));
          threadPool.setMaxSize(BigInteger.valueOf(poolConfig.maxSize()));
          threadPools.add(threadPool);
        });
        configType.setThreadPools(threadPoolsType);
      }
    );
  }
}
