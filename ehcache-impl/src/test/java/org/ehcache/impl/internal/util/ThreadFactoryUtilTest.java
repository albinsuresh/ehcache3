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
package org.ehcache.impl.internal.util;

import org.junit.Test;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * @author Ludovic Orban
 */
public class ThreadFactoryUtilTest {

  @Test
  public void testCreatedThreadIsInFactoryCreatorsThreadGroup() throws Exception {
    final AtomicReference<String> threadGroupName = new AtomicReference<>();
    final ThreadFactory myPool = ThreadFactoryUtil.threadFactory("ThreadFactoryUtilTest-pool");

    ThreadGroup testGroup = new ThreadGroup("ThreadFactoryUtilTest-testGroup");
    Thread threadWithNonDefaultGroup = new Thread(testGroup, () -> {
      Thread poolCreatedThread = myPool.newThread(() -> {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        threadGroupName.set(threadGroup.getName());
      });
      poolCreatedThread.start();
      try {
        poolCreatedThread.join();
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    threadWithNonDefaultGroup.start();
    threadWithNonDefaultGroup.join();

    assertThat(threadGroupName.get(), is(not("ThreadFactoryUtilTest-testGroup")));
  }

}
