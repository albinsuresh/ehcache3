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

package org.ehcache.xml.model;

import org.ehcache.xml.CacheResourceConfigurationParser;
import org.ehcache.xml.CacheServiceConfigurationParser;

import java.net.URI;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

public interface CacheDefinition extends CacheTemplate {

  String id();

  class Impl extends BaseCacheSpec implements CacheDefinition {

    private final String id;

    public Impl(String id, Map<URI, CacheServiceConfigurationParser<?>> serviceConfigParsers,
                Map<URI, CacheResourceConfigurationParser> resourceConfigParsers, Unmarshaller unmarshaller, BaseCacheType... sources) {
      super(serviceConfigParsers, resourceConfigParsers, unmarshaller, sources);
      this.id = id;
    }

    @Override
    public String id() {
      return id;
    }
  }

}
