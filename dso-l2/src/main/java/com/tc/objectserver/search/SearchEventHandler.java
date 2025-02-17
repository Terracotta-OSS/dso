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
package com.tc.objectserver.search;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventContext;
import com.tc.async.api.EventHandlerException;
import com.tc.async.api.MultiThreadedEventContext;
import com.tc.objectserver.core.api.ServerConfigurationContext;
import com.terracottatech.search.IndexException;

import java.io.IOException;

/**
 * All search request are processed through this handler. Every context should implement
 * {@link MultiThreadedEventContext} so that order can be maintained per client.
 * 
 * @author Nabib El-Rahman
 */
public class SearchEventHandler extends AbstractEventHandler {

  private IndexManager indexManager;

  /**
   * {@inheritDoc}
   */
  @Override
  public void handleEvent(EventContext context) throws EventHandlerException {
    if (context instanceof SearchUpsertContext) {
      SearchUpsertContext suc = (SearchUpsertContext) context;

      try {
        if (suc.isInsert()) {
          this.indexManager.insert(suc.getCacheName(), suc.getCacheKey(), suc.getCacheValue(), suc.getAttributes(),
                                   suc.getSegmentOid(), suc.getMetaDataProcessingContext());
        } else {

          this.indexManager.update(suc.getCacheName(), suc.getCacheKey(), suc.getCacheValue(), suc.getAttributes(),
                                   suc.getSegmentOid(), suc.getMetaDataProcessingContext());
        }
      } catch (IndexException e) {
        // TODO: figure out what to do with IndexException, rethrow for now.
        throw new EventHandlerException(e);
      }
    } else if (context instanceof SearchDeleteContext) {
      SearchDeleteContext sdc = (SearchDeleteContext) context;
      try {
        this.indexManager.remove(sdc.getCacheName(), sdc.getCacheKey(), sdc.getSegmentOid(),
                                 sdc.getMetaDataProcessingContext());
      } catch (IndexException e) {
        // TODO: figure out what to do with IndexException, rethrow for now.
        throw new EventHandlerException(e);
      }
    } else if (context instanceof SearchClearContext) {
      SearchClearContext scc = (SearchClearContext) context;
      try {
        this.indexManager.clear(scc.getCacheName(), scc.getSegmentOid(), scc.getMetaDataProcessingContext());
      } catch (IndexException e) {
        // TODO: figure out what to do with IndexException, rethrow for now.
        throw new EventHandlerException(e);
      }
    } else if (context instanceof SearchRemoveIfValueEqualsContext) {
      SearchRemoveIfValueEqualsContext serc = (SearchRemoveIfValueEqualsContext) context;
      try {
        this.indexManager.removeIfValueEqual(serc.getCacheName(), serc.getRemoves(), serc.getSegmentOid(),
                                             serc.getMetaDataProcessingContext(), serc.isEviction());
      } catch (IndexException e) {
        // TODO: figure out what to do with IndexException, rethrow for now.
        throw new EventHandlerException(e);
      }
    } else if (context instanceof SearchReplaceContext) {
      SearchReplaceContext src = (SearchReplaceContext) context;
      try {
        this.indexManager.replace(src.getCacheName(), src.getCacheKey(), src.getCacheValue(), src.getPreviousValue(),
                                  src.getAttributes(), src.getSegmentOid(), src.getMetaDataProcessingContext());
      } catch (IndexException e) {
        // TODO: figure out what to do with IndexException, rethrow for now.
        throw new EventHandlerException(e);
      }
    } else if (context instanceof SearchPutIfAbsentContext) {
      SearchPutIfAbsentContext src = (SearchPutIfAbsentContext) context;
      try {
        this.indexManager.putIfAbsent(src.getCacheName(), src.getCacheKey(), src.getCacheValue(), src.getAttributes(),
                                      src.getSegmentOid(), src.getMetaDataProcessingContext());
      } catch (IndexException e) {
        // TODO: figure out what to do with IndexException, rethrow for now.
        throw new EventHandlerException(e);
      }

    } else if (context instanceof DirectExecuteSearchContext) {
      DirectExecuteSearchContext desc = (DirectExecuteSearchContext) context;
      try {
        desc.execute();
      } catch (IndexException e) {
        // TODO: figure out what to do with IndexException, rethrow for now.
        throw new EventHandlerException(e);
      }
    } else if (context instanceof SearchDestroyContext) {
      try {
        final SearchDestroyContext searchDestroyContext = (SearchDestroyContext)context;
        this.indexManager.deleteIndex(searchDestroyContext.getCacheName(), searchDestroyContext.getMetaDataProcessingContext());
      } catch (IndexException e) {
        // TODO: figure out what to do with IndexException, rethrow for now.
        throw new EventHandlerException(e);
      }
    } else if (context instanceof SearchIndexSnapshotContext) {
      SearchIndexSnapshotContext snapCtxt = (SearchIndexSnapshotContext) context;
      try {
        if (snapCtxt.isClose()) indexManager.releaseSearchResults(snapCtxt.getCacheName(), snapCtxt.getSnapshotId(),
                                                                  snapCtxt.getMetaDataProcessingContext());
        else indexManager.snapshotForQuery(snapCtxt.getCacheName(), snapCtxt.getSnapshotId(),
                                           snapCtxt.getMetaDataProcessingContext());
      } catch (IndexException e) {
        throw new EventHandlerException(e);
      }
    } else {
      throw new AssertionError("Unknown context: " + context);
    }
  }

  @Override
  protected void initialize(ConfigurationContext context) {
    super.initialize(context);
    ServerConfigurationContext serverContext = (ServerConfigurationContext) context;
    this.indexManager = serverContext.getIndexManager();
  }

}
