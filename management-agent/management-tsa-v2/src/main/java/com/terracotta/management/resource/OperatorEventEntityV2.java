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
package com.terracotta.management.resource;

/**
 * A {@link org.terracotta.management.resource.AbstractEntityV2} representing an operator event
 * from the management API.
 *
 * @author Ludovic Orban
 */
public class OperatorEventEntityV2 extends AbstractTsaEntityV2 {

  private String sourceId;
  private long timestamp;
  private String message;
  private String collapseString;
  private String eventSubsystem;
  private String eventLevel;
  private String  eventType;
  private boolean read;

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCollapseString() {
    return collapseString;
  }

  public void setCollapseString(String collapseString) {
    this.collapseString = collapseString;
  }

  public String getEventSubsystem() {
    return eventSubsystem;
  }

  public void setEventSubsystem(String eventSubsystem) {
    this.eventSubsystem = eventSubsystem;
  }

  public String getEventLevel() {
    return eventLevel;
  }

  public void setEventLevel(String eventLevel) {
    this.eventLevel = eventLevel;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  @Override
  public String toString() {
    return String.format("%s-%s:%s:%s:%d", sourceId, eventSubsystem, eventType, message, timestamp);
  }
}
