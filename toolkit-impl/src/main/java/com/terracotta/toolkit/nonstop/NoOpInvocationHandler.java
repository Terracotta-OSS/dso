/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
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
package com.terracotta.toolkit.nonstop;

import org.terracotta.toolkit.search.AggregateFunction;
import org.terracotta.toolkit.search.Attribute;
import org.terracotta.toolkit.search.QueryBuilder;
import org.terracotta.toolkit.search.SortDirection;
import org.terracotta.toolkit.search.ToolkitSearchQuery;
import org.terracotta.toolkit.search.expression.Clause;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.terracotta.toolkit.util.ToolkitInstanceProxy.newToolkitProxy;

class NoOpInvocationHandler implements InvocationHandler {
  private volatile NoOpQueryBuilder queryBuilder;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // no-op and return default for primitive types and null for Objects.
    if (method.getReturnType().isPrimitive()) {
      if (method.getReturnType() == Byte.TYPE) {
        return 0;
      } else if (method.getReturnType() == Short.TYPE) {
        return 0;
      } else if (method.getReturnType() == Integer.TYPE) {
        return 0;
      } else if (method.getReturnType() == Long.TYPE) {
        return 0L;
      } else if (method.getReturnType() == Float.TYPE) {
        return 0.0f;
      } else if (method.getReturnType() == Double.TYPE) {
        return 0.0d;
      } else if (method.getReturnType() == Character.TYPE) {
        return '\u0000';
      } else if (method.getReturnType() == Boolean.TYPE) { return false; }
    } else if (Map.class.isAssignableFrom(method.getReturnType())) {
      return Collections.EMPTY_MAP;
    } else if (List.class.isAssignableFrom(method.getReturnType())) {
      return Collections.EMPTY_LIST;
    } else if (QueryBuilder.class.isAssignableFrom(method.getReturnType())) {
      if (queryBuilder == null) {
        queryBuilder = new NoOpQueryBuilder(this);
      }
      return queryBuilder;
    } else if (Set.class.isAssignableFrom(method.getReturnType())) { return Collections.EMPTY_SET; }
    return null;
  }

  private static class NoOpQueryBuilder implements QueryBuilder {
    private final NoOpInvocationHandler noOpInvocationHandler;

    public NoOpQueryBuilder(NoOpInvocationHandler noOpInvocationHandler) {
      this.noOpInvocationHandler = noOpInvocationHandler;
    }

    @Override
    public QueryBuilder includeKeys(boolean choice) {
      return this;
    }

    @Override
    public QueryBuilder includeValues(boolean choice) {
      return this;
    }

    @Override
    public QueryBuilder maxResults(int max) {
      return this;
    }

    @Override
    public QueryBuilder resultPageSize(int size) {
      return this;
    }

    @Override
    public QueryBuilder includeAttribute(Attribute<?>... attr) {
      return this;
    }

    @Override
    public QueryBuilder addGroupBy(Attribute<?>... attr) {
      return this;
    }

    @Override
    public QueryBuilder addOrderBy(Attribute<?> attr, SortDirection dir) {
      return this;
    }

    @Override
    public QueryBuilder includeAggregator(AggregateFunction... aggregators) {
      return this;
    }

    @Override
    public QueryBuilder addClause(Clause clause) {
      return this;
    }

    @Override
    public ToolkitSearchQuery build() {
      return newToolkitProxy(ToolkitSearchQuery.class, noOpInvocationHandler);
    }

  }
}