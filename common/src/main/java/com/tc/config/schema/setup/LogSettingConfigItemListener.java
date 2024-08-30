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
package com.tc.config.schema.setup;

import com.tc.config.schema.dynamic.ConfigItem;
import com.tc.config.schema.dynamic.ConfigItemListener;
import com.tc.logging.TCLogging;

import java.io.File;

/**
 * Tells {@link TCLogging} to set its log file to the location specified. This must be attached to a {@link ConfigItem}
 * that returns {@link File} objects.
 */
public class LogSettingConfigItemListener implements ConfigItemListener {

  private String logName;

  public LogSettingConfigItemListener(String logName) {
    this.logName = logName;
  }

  @Override
  public void valueChanged(Object oldValue, Object newValue) {
    if (newValue != null) {
      TCLogging.setLogDirectory((File) newValue, logName);
    }
  }

}
