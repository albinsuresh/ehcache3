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

package org.ehcache.clustered.client.internal.store.operations;

import org.ehcache.clustered.client.internal.store.ChainBuilder;
import org.ehcache.clustered.client.internal.store.ResolvedChain;
import org.ehcache.clustered.client.internal.store.operations.codecs.OperationsCodec;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.expiry.Expirations;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChainResolverJMHTest {

  private static Chain getChainFromOperations(OperationsCodec<Long, Person> codec, List<Operation<Long, Person>> operations) {
    ChainBuilder chainBuilder = new ChainBuilder();
    for(Operation<Long, Person> operation: operations) {
      chainBuilder = chainBuilder.add(codec.encode(operation));
    }
    return chainBuilder.build();
  }

  @State(Scope.Benchmark)
  public static class HeterogeneousChainState {

    final Chain chain;
    final ChainResolver<Long, Person> resolver;

    public HeterogeneousChainState() {
      OperationsCodec<Long, Person> codec = new OperationsCodec<Long, Person>(new LongSerializer(), new PersonSerializer());
      ArrayList<Operation<Long, Person>> opsList = new ArrayList<Operation<Long, Person>>();
      for (long i = 0; i < 10; i++) {
        Person person = new Person(i, "Value " + i);
        opsList.add(new PutOperation<Long, Person>(i, person, 0L));
      }
      this.chain = getChainFromOperations(codec, opsList);
      this.resolver = new ChainResolver<Long, Person>(codec, Expirations.noExpiration());
    }
  }

  @State(Scope.Benchmark)
  public static class HomogeneousChainState {

    final Chain chain;
    final ChainResolver<Long, Person> resolver;

    public HomogeneousChainState() {
      OperationsCodec<Long, Person> codec = new OperationsCodec<Long, Person>(new LongSerializer(), new PersonSerializer());
      ArrayList<Operation<Long, Person>> opsList = new ArrayList<Operation<Long, Person>>();
      for (long i = 0; i < 10; i++) {
        Person person = new Person(i, "Value " + i);
        opsList.add(new PutOperation<Long, Person>(0L, person, 0L));
      }
      this.chain = getChainFromOperations(codec, opsList);
      this.resolver = new ChainResolver<Long, Person>(codec, Expirations.noExpiration());
    }
  }

  @State(Scope.Benchmark)
  public static class HybridChainState {

    final Chain chain;
    final ChainResolver<Long, Person> resolver;

    public HybridChainState() {
      OperationsCodec<Long, Person> codec = new OperationsCodec<Long, Person>(new LongSerializer(), new PersonSerializer());
      ArrayList<Operation<Long, Person>> opsList = new ArrayList<Operation<Long, Person>>();
      for (long i = 0, key; i < 10; i++) {
        if(i % 2 == 0) {
          key = 0;
        } else {
          key = i;
        }
        Person person = new Person(i, "Value " + i);
        opsList.add(new PutOperation<Long, Person>(key, person, 0L));
      }
      this.chain = getChainFromOperations(codec, opsList);
      this.resolver = new ChainResolver<Long, Person>(codec, Expirations.noExpiration());
    }
  }

  @Benchmark
  public ResolvedChain measureResolveNonExistentKeyInChain(HeterogeneousChainState state) {
    return state.resolver.resolve(state.chain, 10L, 0L);
  }

  @Benchmark
  public ResolvedChain measureResolveOnlyKeyInChain(HomogeneousChainState state) {
    return state.resolver.resolve(state.chain, 0L, 0L);
  }

  @Benchmark
  public ResolvedChain measureResolveHybridChain(HybridChainState state) {
    return state.resolver.resolve(state.chain, 0L, 0L);
  }

  private static class Person {
    long id;
    String name;

    public Person(final long id, final String name) {
      this.id = id;
      this.name = name;
    }
  }

  private static class PersonSerializer implements Serializer<Person> {

    @Override
    public ByteBuffer serialize(final Person person) throws SerializerException {
      ByteBuffer buffer = ByteBuffer.allocate(8 + person.name.getBytes().length);
      buffer.putLong(person.id);
      buffer.put(person.name.getBytes());
      buffer.flip();
      return buffer;
    }

    @Override
    public Person read(final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
      ByteBuffer buffer = binary.duplicate();
      long id = buffer.getLong();
      byte[] nameBytes = new byte[buffer.remaining()];
      buffer.get(nameBytes);
      return new Person(id, new String(nameBytes));
    }

    @Override
    public boolean equals(final Person person, final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
      ByteBuffer buffer = binary.duplicate();
      if(person.id != buffer.getLong()) {
        return false;
      }

      byte[] nameBytes = new byte[buffer.remaining()];
      buffer.get(nameBytes);
      if(!Arrays.equals(person.name.getBytes(), nameBytes)) {
        return false;
      }

      return true;
    }
  }
}
