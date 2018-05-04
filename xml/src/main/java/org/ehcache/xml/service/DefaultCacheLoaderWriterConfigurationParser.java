package org.ehcache.xml.service;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.impl.config.loaderwriter.DefaultCacheLoaderWriterConfiguration;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.xml.CoreServiceConfigurationParser;
import org.ehcache.xml.model.CacheTemplate;

import static org.ehcache.xml.XmlConfiguration.getClassForName;

public class DefaultCacheLoaderWriterConfigurationParser<K, V> implements CoreServiceConfigurationParser<K, V> {

  @Override
  public CacheConfigurationBuilder<K, V> parseServiceConfiguration(CacheTemplate cacheDefinition, ClassLoader cacheClassLoader,
                                                                   CacheConfigurationBuilder<K, V> cacheBuilder) throws ClassNotFoundException {
    if(cacheDefinition.loaderWriter()!= null) {
      final Class<CacheLoaderWriter<?, ?>> cacheLoaderWriterClass = (Class<CacheLoaderWriter<?, ?>>) getClassForName(cacheDefinition
        .loaderWriter(), cacheClassLoader);
      cacheBuilder = cacheBuilder.add(new DefaultCacheLoaderWriterConfiguration(cacheLoaderWriterClass));
    }
    return cacheBuilder;
  }
}
