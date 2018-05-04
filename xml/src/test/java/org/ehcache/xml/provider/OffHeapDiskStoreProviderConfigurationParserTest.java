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

import org.ehcache.config.Configuration;
import org.ehcache.config.builders.ConfigurationBuilder;
import org.ehcache.impl.config.store.disk.OffHeapDiskStoreProviderConfiguration;
import org.ehcache.spi.service.ServiceCreationConfiguration;
import org.ehcache.xml.ConfigurationParser;
import org.ehcache.xml.XmlConfigurationTest;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import static org.ehcache.config.builders.ConfigurationBuilder.newConfigurationBuilder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

public class OffHeapDiskStoreProviderConfigurationParserTest {

  ClassLoader classLoader = this.getClass().getClassLoader();
  ConfigurationBuilder managerBuilder = newConfigurationBuilder();

  @Test
  public void parseServiceCreationConfiguration() throws SAXException, JAXBException, ParserConfigurationException, IOException {
    final URL resource = XmlConfigurationTest.class.getResource("/configs/resources-caches.xml");
    ConfigurationParser rootParser = new ConfigurationParser(resource.toExternalForm());

    OffHeapDiskStoreProviderConfigurationParser parser = new OffHeapDiskStoreProviderConfigurationParser();
    managerBuilder = parser.parseServiceCreationConfiguration(rootParser.getConfigRoot(), classLoader, managerBuilder);
    Configuration xmlConfig = managerBuilder.build();

    assertThat(xmlConfig.getServiceCreationConfigurations().size(), is(1));

    ServiceCreationConfiguration configuration = xmlConfig.getServiceCreationConfigurations().iterator().next();

    assertThat(configuration, instanceOf(OffHeapDiskStoreProviderConfiguration.class));

    OffHeapDiskStoreProviderConfiguration providerConfiguration = (OffHeapDiskStoreProviderConfiguration) configuration;
    assertThat(providerConfiguration.getThreadPoolAlias(), is("disk-pool"));
  }
}
