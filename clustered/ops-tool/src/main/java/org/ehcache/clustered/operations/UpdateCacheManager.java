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
package org.ehcache.clustered.operations;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.io.File;

@Parameters(commandNames = "update", commandDescription = "update a clustered cache manager, and it's caches to match a new configuration")
class UpdateCacheManager extends AbstractCommand {

  @Parameter(names = {"-c", "--config"}, required = true, description = "updated configuration file")
  private File config;

  @Parameter(names = {"-d", "--allow-destroy"}, description ="allow destruction of caches")
  private boolean destroy = false;

  @Parameter(names = {"-m", "--allow-mutation"}, description ="allow modification of caches")
  private boolean mutation = false;

  UpdateCacheManager(BaseOptions base) {
    super(base);
  }

  @Override
  public int execute() {
    if (getClusterLocationOverride() == null) {
      System.out.println("Updating cache manager to config " + config + (destroy ? " [destroy allowed]" : "") + (mutation ? " [mutate allowed]" : "") + (isDryRun() ? " [dry-run]" : "") + (isDryRun() ? " [matching]" : " [non-matching]"));
    } else {
      System.out.println("Updating cache manager to config " + config + " at overriding location " + getClusterLocationOverride() + (destroy ? " [destroy allowed]" : "") + (mutation ? " [mutate allowed]" : "") + (isDryRun() ? " [dry-run]" : "") + (isDryRun() ? " [matching]" : " [non-matching]"));
    }
    return 0;
  }
}
