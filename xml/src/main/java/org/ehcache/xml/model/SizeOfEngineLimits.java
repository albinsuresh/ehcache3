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

import org.ehcache.config.units.MemoryUnit;
import org.ehcache.xml.JaxbHelper;

import java.math.BigInteger;

public interface SizeOfEngineLimits {

  long getMaxObjectGraphSize();

  long getMaxObjectSize();

  MemoryUnit getUnit();

  class Impl implements SizeOfEngineLimits {

    private final SizeofType sizeoflimits;

    public Impl(SizeofType sizeoflimits) {
      this.sizeoflimits = sizeoflimits;
    }

    @Override
    public long getMaxObjectGraphSize() {
      SizeofType.MaxObjectGraphSize value = sizeoflimits.getMaxObjectGraphSize();
      if (value == null) {
        return new BigInteger(JaxbHelper.findDefaultValue(sizeoflimits, "maxObjectGraphSize")).longValue();
      } else {
        return value.getValue().longValue();
      }
    }

    @Override
    public long getMaxObjectSize() {
      MemoryType value = sizeoflimits.getMaxObjectSize();
      if (value == null) {
        return new BigInteger(JaxbHelper.findDefaultValue(sizeoflimits, "maxObjectSize")).longValue();
      } else {
        return value.getValue().longValue();
      }
    }

    @Override
    public MemoryUnit getUnit() {
      MemoryType value = sizeoflimits.getMaxObjectSize();
      if (value == null) {
        return MemoryUnit.valueOf(new ObjectFactory().createMemoryType().getUnit().value().toUpperCase());
      } else {
        return MemoryUnit.valueOf(value.getUnit().value().toUpperCase());
      }
    }
  }
}
