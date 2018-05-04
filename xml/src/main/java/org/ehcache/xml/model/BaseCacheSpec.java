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

import org.ehcache.config.ResourcePool;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.core.config.SizedResourcePoolImpl;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.xml.CacheResourceConfigurationParser;
import org.ehcache.xml.CacheServiceConfigurationParser;
import org.ehcache.xml.CoreResourceConfigurationParser;
import org.ehcache.xml.JaxbHelper;
import org.ehcache.xml.resource.DefaultResourceConfigurationParser;
import org.w3c.dom.Element;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public class BaseCacheSpec implements CacheTemplate {

  protected final BaseCacheType[] sources;
  private final Map<URI, CacheServiceConfigurationParser<?>> serviceConfigParsers;

  CoreResourceConfigurationParser coreResourceConfigurationParser;

  public BaseCacheSpec(Map<URI, CacheServiceConfigurationParser<?>> serviceConfigParsers,
                       Map<URI, CacheResourceConfigurationParser> resourceConfigParsers, Unmarshaller unmarshaller, BaseCacheType... sources) {
    this.serviceConfigParsers = serviceConfigParsers;
    coreResourceConfigurationParser = new DefaultResourceConfigurationParser(resourceConfigParsers, unmarshaller);
    this.sources = sources;
  }

  @Override
  public String keyType() {
    String value = null;
    for (BaseCacheType source : sources) {
      value = source.getKeyType() != null ? source.getKeyType().getValue() : null;
      if (value != null) break;
    }
    if (value == null) {
      for (BaseCacheType source : sources) {
        value = JaxbHelper.findDefaultValue(source, "keyType");
        if (value != null) break;
      }
    }
    return value;
  }

  @Override
  public String keySerializer() {
    String value = null;
    for (BaseCacheType source : sources) {
      value = source.getKeyType() != null ? source.getKeyType().getSerializer() : null;
      if (value != null) break;
    }
    return value;
  }

  @Override
  public String keyCopier() {
    String value = null;
    for (BaseCacheType source : sources) {
      value = source.getKeyType() != null ? source.getKeyType().getCopier() : null;
      if (value != null) break;
    }
    return value;
  }

  @Override
  public String valueType() {
    String value = null;
    for (BaseCacheType source : sources) {
      value = source.getValueType() != null ? source.getValueType().getValue() : null;
      if (value != null) break;
    }
    if (value == null) {
      for (BaseCacheType source : sources) {
        value = JaxbHelper.findDefaultValue(source, "valueType");
        if (value != null) break;
      }
    }
    return value;
  }

  @Override
  public String valueSerializer() {
    String value = null;
    for (BaseCacheType source : sources) {
      value = source.getValueType() != null ? source.getValueType().getSerializer() : null;
      if (value != null) break;
    }
    return value;
  }

  @Override
  public String valueCopier() {
    String value = null;
    for (BaseCacheType source : sources) {
      value = source.getValueType() != null ? source.getValueType().getCopier() : null;
      if (value != null) break;
    }
    return value;
  }

  @Override
  public String evictionAdvisor() {
    String value = null;
    for (BaseCacheType source : sources) {
      value = source.getEvictionAdvisor();
      if (value != null) break;
    }
    return value;
  }

  @Override
  public Expiry expiry() {
    ExpiryType value = null;
    for (BaseCacheType source : sources) {
      value = source.getExpiry();
      if (value != null) break;
    }
    if (value != null) {
      return new Expiry.Impl(value);
    } else {
      return null;
    }
  }

  @Override
  public String loaderWriter() {
    String configClass = null;
    for (BaseCacheType source : sources) {
      final CacheLoaderWriterType loaderWriter = source.getLoaderWriter();
      if (loaderWriter != null) {
        configClass = loaderWriter.getClazz();
        break;
      }
    }
    return configClass;
  }

  @Override
  public String resilienceStrategy() {
    String resilienceClass = null;
    for (BaseCacheType source : sources) {
      resilienceClass = source.getResilience();
      if (resilienceClass != null) {
        return resilienceClass;
      }
    }
    return resilienceClass;
  }

  @Override
  public ListenersConfig listenersConfig() {
    ListenersType base = null;
    ArrayList<ListenersType> additionals = new ArrayList<>();
    for (BaseCacheType source : sources) {
      if (source.getListeners() != null) {
        if (base == null) {
          base = source.getListeners();
        } else {
          additionals.add(source.getListeners());
        }
      }
    }
    return base != null ? new ListenersConfig.Impl(base, additionals.toArray(new ListenersType[0])) : null;
  }


  @Override
  public Iterable<ServiceConfiguration<?>> serviceConfigs() {
    Map<Class<? extends ServiceConfiguration>, ServiceConfiguration<?>> configsMap =
      new HashMap<>();
    for (BaseCacheType source : sources) {
      for (Element child : source.getServiceConfiguration()) {
        ServiceConfiguration<?> serviceConfiguration = parseCacheExtension(child);
        if (!configsMap.containsKey(serviceConfiguration.getClass())) {
          configsMap.put(serviceConfiguration.getClass(), serviceConfiguration);
        }
      }
    }
    return configsMap.values();
  }

  ServiceConfiguration<?> parseCacheExtension(final Element element) {
    URI namespace = URI.create(element.getNamespaceURI());
    final CacheServiceConfigurationParser<?> xmlConfigurationParser = serviceConfigParsers.get(namespace);
    if(xmlConfigurationParser == null) {
      throw new IllegalArgumentException("Can't find parser for namespace: " + namespace);
    }
    return xmlConfigurationParser.parseServiceConfiguration(element);
  }

  @Override
  public Collection<ResourcePool> resourcePools() {
    for (BaseCacheType source : sources) {
      Heap heapResource = source.getHeap();
      if (heapResource != null) {
        return singleton(parseResource(heapResource));
      } else {
        ResourcesType resources = source.getResources();
        if (resources != null) {
          return parseResources(resources);
        }
      }
    }
    return emptySet();
  }

  private Collection<ResourcePool> parseResources(ResourcesType resources) {
    Collection<ResourcePool> resourcePools = new ArrayList<>();
    for (Element resource : resources.getResource()) {
      resourcePools.add(parseResource(resource));
    }
    return resourcePools;
  }

  private ResourcePool parseResource(Heap resource) {
    ResourceType heapResource = resource.getValue();
    return new SizedResourcePoolImpl<>(org.ehcache.config.ResourceType.Core.HEAP,
      heapResource.getValue().longValue(), parseUnit(heapResource), false);
  }

  private static ResourceUnit parseUnit(ResourceType resourceType) {
    if (resourceType.getUnit().value().equalsIgnoreCase("entries")) {
      return EntryUnit.ENTRIES;
    } else {
      return org.ehcache.config.units.MemoryUnit.valueOf(resourceType.getUnit().value().toUpperCase());
    }
  }

  private ResourcePool parseResource(Element element) {
    return coreResourceConfigurationParser.parseResourceConfiguration(element);
  }

  @Override
  public CacheLoaderWriterType.WriteBehind writeBehind() {
    for (BaseCacheType source : sources) {
      final CacheLoaderWriterType loaderWriter = source.getLoaderWriter();
      final CacheLoaderWriterType.WriteBehind writebehind = loaderWriter != null ? loaderWriter.getWriteBehind() : null;
      if (writebehind != null) {
        return writebehind;
      }
    }
    return null;
  }

  @Override
  public DiskStoreSettingsType diskStoreSettings() {
    DiskStoreSettingsType value = null;
    for (BaseCacheType source : sources) {
      value = source.getDiskStoreSettings();
      if (value != null) break;
    }
    return value;
  }

  @Override
  public SizeOfEngineLimits heapStoreSettings() {
    SizeofType sizeofType = null;
    for (BaseCacheType source : sources) {
      sizeofType = source.getHeapStoreSettings();
      if (sizeofType != null) break;
    }
    return sizeofType != null ? new SizeOfEngineLimits.Impl(sizeofType) : null;
  }
}
