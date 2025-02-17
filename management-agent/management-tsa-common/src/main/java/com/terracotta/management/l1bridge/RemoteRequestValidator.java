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
package com.terracotta.management.l1bridge;

import org.terracotta.management.ServiceExecutionException;
import org.terracotta.management.resource.Representable;
import org.terracotta.management.resource.exceptions.ResourceRuntimeException;
import org.terracotta.management.resource.services.validator.RequestValidator;

import com.terracotta.management.service.L1MBeansSource;
import com.terracotta.management.service.RemoteAgentBridgeService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author Ludovic Orban
 */
public class RemoteRequestValidator implements RequestValidator {

  private final RemoteAgentBridgeService remoteAgentBridgeService;
  private final L1MBeansSource l1MBeansSource;

  private static final ThreadLocal<Set<String>> tlNode = new ThreadLocal<Set<String>>();

  public RemoteRequestValidator(RemoteAgentBridgeService remoteAgentBridgeService, L1MBeansSource l1MBeansSource) {
    this.remoteAgentBridgeService = remoteAgentBridgeService;
    this.l1MBeansSource = l1MBeansSource;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validateSafe(UriInfo info) {
    validateAgentSegment(info.getPathSegments());
  }

  @Override
  public void validate(UriInfo info) {
    validateAgentSegment(info.getPathSegments());
  }

  protected void validateAgentSegment(List<PathSegment> pathSegments) {
    if (!l1MBeansSource.containsJmxMBeans()) {
      // no validation is required on this server as the request is going to be
      // forwarded to another one
      return;
    }

    String ids = getAgentIdsFromPathSegments(pathSegments);

    try {
      Set<String> nodes = remoteAgentBridgeService.getRemoteAgentNodeNames();
      if (ids == null) {
        setValidatedNodes(nodes);
      } else {
        String[] idsArray = ids.split(",");

        for (String id : idsArray) {
          if (!nodes.contains(id) && !Representable.EMBEDDED_AGENT_ID.equals(id)) {
            throw new ResourceRuntimeException(
                String.format("Agent IDs must be in '%s' or '%s'.", nodes,
                Representable.EMBEDDED_AGENT_ID),
                Response.Status.BAD_REQUEST.getStatusCode());
          }
        }

        setValidatedNodes(new HashSet<String>(Arrays.asList(idsArray)));
      }
    } catch (ServiceExecutionException see) {
      throw new ResourceRuntimeException(
          "Unexpected error validating request.",
          see,
          Response.Status.BAD_REQUEST.getStatusCode());
    }
  }

  String getAgentIdsFromPathSegments(List<PathSegment> pathSegments) {
    for (PathSegment pathSegment : pathSegments) {
      String path = pathSegment.getPath();
      if (path.equals("agents")) {
        return pathSegment.getMatrixParameters().getFirst("ids");
      }
    }
    return null;
  }

  public Set<String> getValidatedNodes() {
    return tlNode.get();
  }

  public String getSingleValidatedNode() {
    if (tlNode.get().size() != 1) {
      throw new RuntimeException("A single node ID must be specified, got: " + tlNode.get());
    }
    return tlNode.get().iterator().next();
  }

  public void setValidatedNodes(Set<String> node) {
    tlNode.set(node);
  }

}
