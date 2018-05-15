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

package org.ehcache.xml.service;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.impl.config.event.DefaultCacheEventListenerConfiguration;
import org.ehcache.xml.CoreServiceConfigurationParser;
import org.ehcache.xml.model.CacheTemplate;
import org.ehcache.xml.model.CacheType;
import org.ehcache.xml.model.EventFiringType;
import org.ehcache.xml.model.EventOrderingType;
import org.ehcache.xml.model.EventType;
import org.ehcache.xml.model.ListenersConfig;
import org.ehcache.xml.model.ListenersType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.ehcache.core.spi.service.ServiceUtils.findAmongst;
import static org.ehcache.xml.XmlConfiguration.getClassForName;

public class DefaultCacheEventListenerConfigurationParser implements CoreServiceConfigurationParser {

  @Override
  public <K, V> CacheConfigurationBuilder<K, V> parseServiceConfiguration(CacheTemplate cacheDefinition, ClassLoader cacheClassLoader,
                                                                          CacheConfigurationBuilder<K, V> cacheBuilder) throws ClassNotFoundException {
    ListenersConfig listenersConfig = cacheDefinition.listenersConfig();
    if(listenersConfig != null && listenersConfig.listeners() != null) {
      for (ListenersType.Listener listener : listenersConfig.listeners()) {
        Set<org.ehcache.event.EventType> eventSetToFireOn = listener.getEventsToFireOn().stream()
          .map(EventType::value).map(org.ehcache.event.EventType::valueOf).collect(toSet());
        @SuppressWarnings("unchecked")
        Class<CacheEventListener<?, ?>> cacheEventListenerClass = (Class<CacheEventListener<?, ?>>) getClassForName(listener.getClazz(), cacheClassLoader);
        CacheEventListenerConfigurationBuilder listenerBuilder = CacheEventListenerConfigurationBuilder
          .newEventListenerConfiguration(cacheEventListenerClass, eventSetToFireOn)
          .firingMode(EventFiring.valueOf(listener.getEventFiringMode().value()))
          .eventOrdering(EventOrdering.valueOf(listener.getEventOrderingMode().value()));
        cacheBuilder = cacheBuilder.add(listenerBuilder);
      }
    }

    return cacheBuilder;
  }

  @Override
  public void unparseServiceConfiguration(CacheType cacheType, CacheConfiguration<?, ?> cacheConfiguration) {
    Collection<DefaultCacheEventListenerConfiguration> serviceConfigs =
      findAmongst(DefaultCacheEventListenerConfiguration.class, cacheConfiguration.getServiceConfigurations());

    if (!serviceConfigs.isEmpty()) {
      ListenersType listenersType = cacheType.getListeners();
      if (listenersType == null) {
        listenersType = new ListenersType();
        cacheType.setListeners(listenersType);
      }

      List<ListenersType.Listener> listeners = listenersType.getListener();
      for (DefaultCacheEventListenerConfiguration serviceConfig : serviceConfigs) {
        ListenersType.Listener listener = new ListenersType.Listener();
        listener.setClazz(serviceConfig.getClazz().getName());
        listener.setEventFiringMode(EventFiringType.fromValue(serviceConfig.firingMode().name()));
        listener.setEventOrderingMode(EventOrderingType.fromValue(serviceConfig.orderingMode().name()));
        List<EventType> eventsToFireOn = listener.getEventsToFireOn();
        for (org.ehcache.event.EventType eventType : serviceConfig.fireOn()) {
          eventsToFireOn.add(EventType.fromValue(eventType.name()));
        }
        listeners.add(listener);
      }
    }

  }
}

