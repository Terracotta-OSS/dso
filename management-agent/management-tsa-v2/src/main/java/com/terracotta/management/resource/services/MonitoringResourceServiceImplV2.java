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
package com.terracotta.management.resource.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.ServiceExecutionException;
import org.terracotta.management.ServiceLocator;
import org.terracotta.management.resource.ResponseEntityV2;
import org.terracotta.management.resource.exceptions.ResourceRuntimeException;
import org.terracotta.management.resource.services.validator.RequestValidator;

import com.terracotta.management.resource.StatisticsEntityV2;
import com.terracotta.management.resource.services.utils.UriInfoUtils;
import com.terracotta.management.service.MonitoringServiceV2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * A resource service for querying TSA runtime statistics.
 * 
 * @author Ludovic Orban
 */
@Path("/v2/agents/statistics")
public class MonitoringResourceServiceImplV2 {

  private static final Logger LOG = LoggerFactory.getLogger(MonitoringResourceServiceImplV2.class);

  private final MonitoringServiceV2 monitoringService;
  private final RequestValidator requestValidator;

  public MonitoringResourceServiceImplV2() {
    this.monitoringService = ServiceLocator.locate(MonitoringServiceV2.class);
    this.requestValidator = ServiceLocator.locate(RequestValidator.class);
  }

  public final static String ATTR_QUERY_KEY = "show";

  /**
   * Get a {@code Collection} of {@link StatisticsEntityV2} objects representing the server(s) statistics provided by the
   * associated monitorable entity's agent given the request path.
   *
   * @return a a collection of {@link StatisticsEntityV2} objects.
   */
  @GET
  @Path("/servers")
  @Produces(MediaType.APPLICATION_JSON)
  public ResponseEntityV2<StatisticsEntityV2> getServerStatistics(@Context UriInfo info) {
    LOG.debug(String.format("Invoking MonitoringResourceServiceImplV2.getServerStatistics: %s", info.getRequestUri()));

    requestValidator.validateSafe(info);

    try {
      Set<String> serverNames = UriInfoUtils.extractLastSegmentMatrixParameterAsSet(info, "names");

      MultivaluedMap<String, String> qParams = info.getQueryParameters();
      List<String> attrs = qParams.get(ATTR_QUERY_KEY);
      Set<String> tsaAttrs = attrs == null || attrs.isEmpty() ? null : new HashSet<String>(attrs);

      return monitoringService.getServerStatistics(serverNames, tsaAttrs);
    } catch (ServiceExecutionException see) {
      throw new ResourceRuntimeException("Failed to get TSA statistics", see, Response.Status.BAD_REQUEST.getStatusCode());
    }
  }

  /**
   * Get a {@code Collection} of {@link StatisticsEntityV2} objects representing the DGC statistics of each iteration
   * provided by the associated monitorable entity's agent given the request path.
   *
   * @return a a collection of {@link StatisticsEntityV2} objects.
   */
  @GET
  @Path("/dgc")
  @Produces(MediaType.APPLICATION_JSON)
  public ResponseEntityV2<StatisticsEntityV2> getDgcStatistics(@Context UriInfo info) {
    LOG.debug(String.format("Invoking MonitoringResourceServiceImplV2.getDgcStatistics: %s", info.getRequestUri()));

    requestValidator.validateSafe(info);

    try {
      Set<String> serverNames = UriInfoUtils.extractLastSegmentMatrixParameterAsSet(info, "serverNames");

      return monitoringService.getDgcStatistics(serverNames);
    } catch (ServiceExecutionException see) {
      throw new ResourceRuntimeException("Failed to get TSA statistics", see, Response.Status.BAD_REQUEST.getStatusCode());
    }
  }

  /**
   * Get a {@code Collection} of {@link StatisticsEntityV2} objects representing the client(s) statistics provided by the
   * associated monitorable entity's agent given the request path.
   *
   * @return a a collection of {@link StatisticsEntityV2} objects.
   */
  @GET
  @Path("/clients")
  @Produces(MediaType.APPLICATION_JSON)
  public ResponseEntityV2<StatisticsEntityV2> getClientStatistics(@Context UriInfo info) {
    LOG.debug(String.format("Invoking MonitoringResourceServiceImplV2.getClientStatistics: %s", info.getRequestUri()));

    requestValidator.validateSafe(info);

    try {
      Set<String> clientIds = UriInfoUtils.extractLastSegmentMatrixParameterAsSet(info, "ids");

      MultivaluedMap<String, String> qParams = info.getQueryParameters();
      List<String> attrs = qParams.get(ATTR_QUERY_KEY);
      Set<String> tsaAttrs = attrs == null || attrs.isEmpty() ? null : new HashSet<String>(attrs);

      Set<String> productIDs = UriInfoUtils.extractProductIds(info);

      return monitoringService.getClientStatistics(clientIds, tsaAttrs, productIDs);
    } catch (ServiceExecutionException see) {
      throw new ResourceRuntimeException("Failed to get TSA statistics", see, Response.Status.BAD_REQUEST.getStatusCode());
    }
  }

}
