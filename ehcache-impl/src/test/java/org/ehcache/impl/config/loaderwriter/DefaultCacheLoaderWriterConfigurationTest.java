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

package org.ehcache.impl.config.loaderwriter;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

public class DefaultCacheLoaderWriterConfigurationTest {

  @Test
  public void testDeriveDetachesCorrectly() {
    CacheLoaderWriter<?, ?> mock = mock(CacheLoaderWriter.class);
    DefaultCacheLoaderWriterConfiguration configuration = new DefaultCacheLoaderWriterConfiguration(mock);
    DefaultCacheLoaderWriterConfiguration derived = configuration.build(configuration.derive());

    assertThat(derived, is(not(sameInstance(configuration))));
    assertThat(derived.getInstance(), sameInstance(configuration.getInstance()));
  }
}
