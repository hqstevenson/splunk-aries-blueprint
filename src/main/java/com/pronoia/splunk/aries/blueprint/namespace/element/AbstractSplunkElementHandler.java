/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pronoia.splunk.aries.blueprint.namespace.element;

import com.pronoia.aries.blueprint.util.namespace.AbstractElementHandler;
import com.pronoia.aries.blueprint.util.parser.ElementParser;
import com.pronoia.aries.blueprint.util.reflect.MapMetadataUtil;
import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import java.util.List;
import java.util.Map;

import org.apache.aries.blueprint.mutable.MutableMapMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractSplunkElementHandler extends AbstractElementHandler {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public AbstractSplunkElementHandler(SplunkNamespaceHandler namespaceHandler, String elementTagName) {
        super(namespaceHandler, elementTagName);
    }


    @Override
    protected SplunkNamespaceHandler getNamespaceHandler() {
        return (SplunkNamespaceHandler) super.getNamespaceHandler();
    }

    protected void addDefaultSplunkClientId(Map<String, String> attributeValues) {
        if (attributeValues != null) {
            if (getNamespaceHandler().hasDefaultSplunkClientId()) {
                attributeValues.putIfAbsent("splunk-client-id", getNamespaceHandler().getDefaultSplunkClientId());
            }
        }
    }

    protected MutableMapMetadata parseSystemProperties(ElementParser elementParser) {
        MutableMapMetadata systemPropertyMetadata = MapMetadataUtil.create(String.class, String.class);

        List<ElementParser> systemPropertyElements = elementParser.getElements("system-property");
        if (systemPropertyElements != null && !systemPropertyElements.isEmpty()) {

            for (ElementParser systemPropertyElement : systemPropertyElements) {
                String property = systemPropertyElement.getAttribute("property", true);
                String field = systemPropertyElement.getAttribute("field");
                if (field != null && !field.isEmpty()) {
                    MapMetadataUtil.addValue(systemPropertyMetadata, property, field);
                } else {
                    MapMetadataUtil.addValue(systemPropertyMetadata, property, property);
                }
            }
        }

        return systemPropertyMetadata;
    }


    protected MutableMapMetadata parseConstantFields(ElementParser elementParser) {
        MutableMapMetadata constantFieldMetadata = MapMetadataUtil.create(String.class, String.class);

        List<ElementParser> constantFieldElements = elementParser.getElements("constant-field");
        if (constantFieldElements != null && !constantFieldElements.isEmpty()) {

            for (ElementParser constantFieldElement : constantFieldElements) {
                String field = constantFieldElement.getAttribute("field", true);
                String value = constantFieldElement.getAttribute("value", true);
                MapMetadataUtil.addValue(constantFieldMetadata, field, value);
            }
        }

        return constantFieldMetadata;
    }
}
