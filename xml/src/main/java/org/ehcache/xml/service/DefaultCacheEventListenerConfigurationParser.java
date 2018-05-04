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

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.xml.CoreServiceConfigurationParser;
import org.ehcache.xml.model.CacheTemplate;
import org.ehcache.xml.model.EventType;
import org.ehcache.xml.model.ListenersConfig;
import org.ehcache.xml.model.ListenersType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.ehcache.xml.XmlConfiguration.getClassForName;

public class DefaultCacheEventListenerConfigurationParser<K, V> implements CoreServiceConfigurationParser<K, V> {

  @Override
  public CacheConfigurationBuilder<K, V> parseServiceConfiguration(CacheTemplate cacheDefinition, ClassLoader cacheClassLoader,
                                                                   CacheConfigurationBuilder<K, V> cacheBuilder) throws ClassNotFoundException {
    ListenersConfig listenersConfig = cacheDefinition.listenersConfig();
    if(listenersConfig != null && listenersConfig.listeners() != null) {
      for (ListenersType.Listener listener : listenersConfig.listeners()) {
        @SuppressWarnings("unchecked")
        final Class<CacheEventListener<?, ?>> cacheEventListenerClass =
          (Class<CacheEventListener<?, ?>>) getClassForName(listener.getClazz(), cacheClassLoader);
        final List<EventType> eventListToFireOn = listener.getEventsToFireOn();
        Set<org.ehcache.event.EventType> eventSetToFireOn = new HashSet<>();
        for (EventType events : eventListToFireOn) {
          switch (events) {
            case CREATED:
              eventSetToFireOn.add(org.ehcache.event.EventType.CREATED);
              break;
            case EVICTED:
              eventSetToFireOn.add(org.ehcache.event.EventType.EVICTED);
              break;
            case EXPIRED:
              eventSetToFireOn.add(org.ehcache.event.EventType.EXPIRED);
              break;
            case UPDATED:
              eventSetToFireOn.add(org.ehcache.event.EventType.UPDATED);
              break;
            case REMOVED:
              eventSetToFireOn.add(org.ehcache.event.EventType.REMOVED);
              break;
            default:
              throw new IllegalArgumentException("Invalid Event Type provided");
          }
        }
        CacheEventListenerConfigurationBuilder listenerBuilder = CacheEventListenerConfigurationBuilder
          .newEventListenerConfiguration(cacheEventListenerClass, eventSetToFireOn)
          .firingMode(EventFiring.valueOf(listener.getEventFiringMode().value()))
          .eventOrdering(EventOrdering.valueOf(listener.getEventOrderingMode().value()));
        cacheBuilder = cacheBuilder.add(listenerBuilder);
      }
    }

    return cacheBuilder;
  }
}

