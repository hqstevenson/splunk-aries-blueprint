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
package com.pronoia.splunk.aries.blueprint.namespace.element;

import java.util.List;

import com.pronoia.aries.blueprint.util.namespace.AbstractElementHandler;
import com.pronoia.aries.blueprint.util.parser.ElementParser;

import com.pronoia.splunk.aries.blueprint.metadata.JmxAttributeListEventBuilderMetadata;
import com.pronoia.splunk.aries.blueprint.metadata.SplunkJmxAttributeChangeMonitorMetadata;

import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import org.osgi.service.blueprint.reflect.Metadata;


public class SplunkJmxAttributeChangeMonitorElementHandler extends AbstractElementHandler {
    public SplunkJmxAttributeChangeMonitorElementHandler(SplunkNamespaceHandler namespaceHandler, String elementTagName) {
        super(namespaceHandler, elementTagName);
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        SplunkJmxAttributeChangeMonitorMetadata answer = new SplunkJmxAttributeChangeMonitorMetadata(getNamespaceHandler());

        answer.setAttributes(handledElementParser.getAttributeValueMap(), true);

        List<String> observedObjects = handledElementParser.getElementValues("observed-object", true);
        answer.setObservedObjects(observedObjects);

        ElementParser observedAttributesElement = handledElementParser.getElement("observed-attributes");
        if (observedAttributesElement != null) {
            List<String> observedAttributes = observedAttributesElement.getElementValues("attribute");
            answer.setObservedAttributes(observedAttributes);
        }

        ElementParser collectedAttributesElement = handledElementParser.getElement("collected-attributes");
        if (collectedAttributesElement != null) {
            List<String> collectedAttributes = collectedAttributesElement.getElementValues("attribute");
            answer.setCollectedAttributes(collectedAttributes);
        }

        ElementParser excludedAttributesElement = handledElementParser.getElement("excluded-attributes");
        if (excludedAttributesElement != null) {
            List<String> excludedAttributes = excludedAttributesElement.getElementValues("attribute");
            answer.setExcludedAttributes(excludedAttributes);
        }

        ElementParser splunkEventBuilderElement = handledElementParser.getElement("splunk-event");
        if (splunkEventBuilderElement != null) {
            JmxAttributeListEventBuilderMetadata builderMetadata = new JmxAttributeListEventBuilderMetadata();
            builderMetadata.setAttributes(splunkEventBuilderElement.getAttributeValueMap(), true);

            if (answer.hasCollectedAttributes()) {
                builderMetadata.setCollectedAttributes(answer.getCollectedAttributes());
            }
            List<ElementParser> systemPropertyElements = splunkEventBuilderElement.getElements("system-property");
            if (systemPropertyElements != null && !systemPropertyElements.isEmpty()) {
                for (ElementParser systemPropertyElement : systemPropertyElements) {
                    String property = systemPropertyElement.getAttribute("property", true);
                    String field = systemPropertyElement.getAttribute("field");
                    if (field == null || field.isEmpty()) {
                        builderMetadata.addSystemProperty(property);
                    } else {
                        builderMetadata.addSystemProperty(property, field);
                    }
                }
            }
            answer.setEventBuilderMetadata(builderMetadata);
        }

        return answer;
    }

    @Override
    protected SplunkNamespaceHandler getNamespaceHandler() {
        return (SplunkNamespaceHandler) super.getNamespaceHandler();
    }

}
