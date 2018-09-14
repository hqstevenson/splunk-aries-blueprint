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

import java.util.List;
import java.util.Map;

import com.pronoia.aries.blueprint.util.parser.ElementParser;

import com.pronoia.splunk.aries.blueprint.metadata.JmxAttributeListEventBuilderMetadata;
import com.pronoia.splunk.aries.blueprint.metadata.SplunkJmxAttributeChangeMonitorMetadata;

import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import org.osgi.service.blueprint.reflect.Metadata;


public class SplunkJmxAttributeChangeMonitorElementHandler extends AbstractSplunkElementHandler {
    public SplunkJmxAttributeChangeMonitorElementHandler(SplunkNamespaceHandler namespaceHandler, String elementTagName) {
        super(namespaceHandler, elementTagName);
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        SplunkJmxAttributeChangeMonitorMetadata answer = new SplunkJmxAttributeChangeMonitorMetadata();

        Map<String, String> attributeValues = handledElementParser.getAttributeValueMap();
        addDefaultSplunkClientId(attributeValues);
        answer.addProperties(attributeValues, true);

        List<String> observedObjects = handledElementParser.getElementValues("observed-object", true);
        answer.setObservedObjects(observedObjects);

        ElementParser observedAttributesElement = handledElementParser.getElement("observed-attributes");
        if (observedAttributesElement != null) {
            List<String> observedAttributes = observedAttributesElement.getElementValues("attribute");
            answer.setObservedAttributes(observedAttributes);
        }

        List<String> collectedAttributes = null;
        ElementParser collectedAttributesElement = handledElementParser.getElement("collected-attributes");
        if (collectedAttributesElement != null) {
            collectedAttributes = collectedAttributesElement.getElementValues("attribute");
            answer.setCollectedAttributes(collectedAttributes);
        }

        ElementParser excludedAttributesElement = handledElementParser.getElement("excluded-attributes");
        if (excludedAttributesElement != null) {
            List<String> excludedAttributes = excludedAttributesElement.getElementValues("attribute");
            answer.setExcludedAttributes(excludedAttributes);
        }

        ElementParser splunkEventConfigurationElement = handledElementParser.getElement("splunk-event");
        if (splunkEventConfigurationElement != null) {
            JmxAttributeListEventBuilderMetadata eventBuilderMetadata = new JmxAttributeListEventBuilderMetadata();
            eventBuilderMetadata.addProperties(splunkEventConfigurationElement.getAttributeValueMap(), true);

            // TODO:  Figure out why this line causes a second observedAttributes property to be set on the answer, and why it has both collected and observed attributes
//            if (collectedAttributes != null) {
//                eventBuilderMetadata.setCollectedAttributes(collectedAttributes);
//            }

            eventBuilderMetadata.setConstantFields(parseConstantFields(splunkEventConfigurationElement));
            eventBuilderMetadata.setSystemProperties(parseSystemProperties(splunkEventConfigurationElement));

            answer.setEventBuilderMetadata(eventBuilderMetadata);
        }

        log.debug("Returning metadata for Splunk JMX Attribute Change Monitor: {}", answer);

        return answer;
    }
}
