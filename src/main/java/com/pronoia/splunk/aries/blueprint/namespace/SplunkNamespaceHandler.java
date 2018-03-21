/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pronoia.splunk.aries.blueprint.namespace;

import com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandler;

import com.pronoia.splunk.aries.blueprint.namespace.element.SplunkClientElementHandler;
import com.pronoia.splunk.aries.blueprint.namespace.element.SplunkJmxAttributeChangeMonitorElementHandler;
import com.pronoia.splunk.aries.blueprint.namespace.element.SplunkJmxNotificationListenerElementHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplunkNamespaceHandler extends AbstractNamespaceHandler {
    Logger log = LoggerFactory.getLogger(this.getClass());

    String defaultSplunkClientId = "default";

    public SplunkNamespaceHandler() {
        addElementHandler(new SplunkClientElementHandler(this, "splunk-client"));
        addElementHandler(new SplunkJmxAttributeChangeMonitorElementHandler(this, "splunk-jmx-attribute-change-monitor"));
        addElementHandler(new SplunkJmxNotificationListenerElementHandler(this, "splunk-jmx-notification-listener"));
    }

    @Override
    protected String getSchemaResourcePath() {
        return "/META-INF/schema/splunk-blueprint.xsd";
    }

    @Override
    protected String getSchema() {
        return "urn:pronoia.com/schema/blueprint/splunk";
    }

    public boolean hasDefaultSplunkClientId() {
        return defaultSplunkClientId != null && !defaultSplunkClientId.isEmpty();
    }

    public String getDefaultSplunkClientId() {
        return defaultSplunkClientId;
    }

    public void setDefaultSplunkClientId(String defaultSplunkClientId) {
        this.defaultSplunkClientId = defaultSplunkClientId;
    }

    public void init() {
        // Placeholder - may not need a body
    }
}
