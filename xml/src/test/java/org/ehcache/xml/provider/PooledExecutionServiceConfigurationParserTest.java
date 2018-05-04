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
import org.ehcache.impl.config.executor.PooledExecutionServiceConfiguration;
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

public class PooledExecutionServiceConfigurationParserTest {

  ClassLoader classLoader = this.getClass().getClassLoader();
  ConfigurationBuilder managerBuilder = newConfigurationBuilder();

  @Test
  public void parseServiceCreationConfiguration() throws SAXException, JAXBException, ParserConfigurationException, IOException, ClassNotFoundException {
    final URL resource = XmlConfigurationTest.class.getResource("/configs/thread-pools.xml");
    ConfigurationParser rootParser = new ConfigurationParser(resource.toExternalForm());

    PooledExecutionServiceConfigurationParser parser = new PooledExecutionServiceConfigurationParser();
    managerBuilder = parser.parseServiceCreationConfiguration(rootParser.getConfigRoot(), classLoader, managerBuilder);
    Configuration xmlConfig = managerBuilder.build();

    assertThat(xmlConfig.getServiceCreationConfigurations().size(), is(1));

    ServiceCreationConfiguration configuration = xmlConfig.getServiceCreationConfigurations().iterator().next();
    assertThat(configuration, instanceOf(PooledExecutionServiceConfiguration.class));
    assertThat(xmlConfig.getServiceCreationConfigurations(), contains(instanceOf(PooledExecutionServiceConfiguration.class)));

    PooledExecutionServiceConfiguration providerConfiguration = (PooledExecutionServiceConfiguration) configuration;
    assertThat(providerConfiguration.getPoolConfigurations().keySet(), containsInAnyOrder("big", "small"));

    PooledExecutionServiceConfiguration.PoolConfiguration small = providerConfiguration.getPoolConfigurations().get("small");
    assertThat(small.minSize(), is(1));
    assertThat(small.maxSize(), is(1));

    PooledExecutionServiceConfiguration.PoolConfiguration big = providerConfiguration.getPoolConfigurations().get("big");
    assertThat(big.minSize(), is(4));
    assertThat(big.maxSize(), is(32));

    assertThat(providerConfiguration.getDefaultPoolAlias(), is("big"));

  }
}
