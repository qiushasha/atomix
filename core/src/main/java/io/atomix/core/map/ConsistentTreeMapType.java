/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.map;

import com.google.common.collect.Maps;
import io.atomix.core.map.impl.CommitResult;
import io.atomix.core.map.impl.ConsistentTreeMapProxyBuilder;
import io.atomix.core.map.impl.DefaultConsistentTreeMapService;
import io.atomix.core.map.impl.MapEntryUpdateResult;
import io.atomix.core.map.impl.MapUpdate;
import io.atomix.core.map.impl.PrepareResult;
import io.atomix.core.map.impl.RollbackResult;
import io.atomix.core.transaction.TransactionId;
import io.atomix.core.transaction.TransactionLog;
import io.atomix.primitive.PrimitiveManagementService;
import io.atomix.primitive.PrimitiveType;
import io.atomix.primitive.service.PrimitiveService;
import io.atomix.primitive.service.ServiceConfig;
import io.atomix.utils.serializer.KryoNamespace;
import io.atomix.utils.serializer.KryoNamespaces;
import io.atomix.utils.serializer.Namespace;
import io.atomix.utils.time.Versioned;

import java.util.AbstractMap;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Consistent tree map primitive type.
 */
public class ConsistentTreeMapType<V>
    implements PrimitiveType<ConsistentTreeMapBuilder<V>, ConsistentTreeMapConfig, ConsistentTreeMap<V>, ServiceConfig> {
  private static final String NAME = "consistent-tree-map";

  /**
   * Returns a new consistent tree map type.
   *
   * @param <V> the value type
   * @return a new consistent tree map type
   */
  public static <V> ConsistentTreeMapType<V> instance() {
    return new ConsistentTreeMapType<>();
  }

  @Override
  public Namespace namespace() {
    return KryoNamespace.builder()
        .register((KryoNamespace) PrimitiveType.super.namespace())
        .register(KryoNamespaces.BASIC)
        .nextId(KryoNamespaces.BEGIN_USER_CUSTOM_ID)
        .register(TransactionId.class)
        .register(TransactionLog.class)
        .register(MapUpdate.class)
        .register(MapUpdate.Type.class)
        .register(PrepareResult.class)
        .register(CommitResult.class)
        .register(RollbackResult.class)
        .register(MapEntryUpdateResult.class)
        .register(MapEntryUpdateResult.Status.class)
        .register(Versioned.class)
        .register(MapEvent.class)
        .register(MapEvent.Type.class)
        .register(byte[].class)
        .register(AbstractMap.SimpleImmutableEntry.class)
        .register(Maps.immutableEntry("", "").getClass())
        .build();
  }

  @Override
  public String id() {
    return NAME;
  }

  @Override
  public PrimitiveService newService(ServiceConfig config) {
    return new DefaultConsistentTreeMapService(config);
  }

  @Override
  public ConsistentTreeMapBuilder<V> newPrimitiveBuilder(String name, PrimitiveManagementService managementService) {
    return newPrimitiveBuilder(name, new ConsistentTreeMapConfig(), managementService);
  }

  @Override
  public ConsistentTreeMapBuilder<V> newPrimitiveBuilder(String name, ConsistentTreeMapConfig config, PrimitiveManagementService managementService) {
    return new ConsistentTreeMapProxyBuilder<>(name, config, managementService);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
        .add("id", id())
        .toString();
  }
}
