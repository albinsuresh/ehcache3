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
package org.ehcache.impl.internal.loaderwriter.writebehind;

import java.time.Duration;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;

import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;

/**
 * @author Abhilash
 *
 */
public class WriteBehindTest extends AbstractWriteBehindTestBase {

  @Override
  protected CacheConfigurationBuilder<String, String> configurationBuilder() {
    return newCacheConfigurationBuilder(String.class, String.class, heap(100))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
  }

  @Override
  protected CacheManagerBuilder<CacheManager> managerBuilder() {
    return newCacheManagerBuilder();
  }
}
