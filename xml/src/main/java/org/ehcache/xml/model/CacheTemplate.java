package org.ehcache.xml.model;

import org.ehcache.config.ResourcePool;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.xml.CacheResourceConfigurationParser;
import org.ehcache.xml.CacheServiceConfigurationParser;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

public interface CacheTemplate {

  String keyType();

  String keySerializer();

  String keyCopier();

  String valueType();

  String valueSerializer();

  String valueCopier();

  String evictionAdvisor();

  Expiry expiry();

  String loaderWriter();

  String resilienceStrategy();

  ListenersConfig listenersConfig();

  Iterable<ServiceConfiguration<?>> serviceConfigs();

  Collection<ResourcePool> resourcePools();

  CacheLoaderWriterType.WriteBehind writeBehind();

  DiskStoreSettingsType diskStoreSettings();

  SizeOfEngineLimits heapStoreSettings();

  class Impl extends BaseCacheSpec {
    public Impl(Map<URI, CacheServiceConfigurationParser<?>> serviceConfigParsers,
                Map<URI, CacheResourceConfigurationParser> resourceConfigParsers, Unmarshaller unmarshaller, CacheTemplateType cacheTemplateType) {
      super(serviceConfigParsers, resourceConfigParsers, unmarshaller, cacheTemplateType);
    }
  }

}
