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

import com.pronoia.aries.blueprint.util.namespace.AbstractElementHandler;
import com.pronoia.aries.blueprint.util.parser.ElementParser;
import com.pronoia.splunk.aries.blueprint.metadata.ActiveMQMessageEventBuilderMetadata;
import com.pronoia.splunk.aries.blueprint.metadata.SplunkEmbeddedActiveMqMessageConsumerFactoryMetadata;
import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import org.osgi.service.blueprint.reflect.Metadata;


public class SplunkEmbeddedActiveMqMessageConsumerFactoryElementHandler extends AbstractElementHandler {
    public SplunkEmbeddedActiveMqMessageConsumerFactoryElementHandler(SplunkNamespaceHandler namespaceHandler, String elementTagName) {
        super(namespaceHandler, elementTagName);
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        SplunkEmbeddedActiveMqMessageConsumerFactoryMetadata answer = new SplunkEmbeddedActiveMqMessageConsumerFactoryMetadata(getNamespaceHandler());

        Map<String, String> attributeValueMap = handledElementParser.getAttributeValueMap();
        answer.setAttributes(handledElementParser.getAttributeValueMap(), true);

        ElementParser destinationPattern = handledElementParser.getElement("destination-pattern", true);
        answer.setDestinationNamePattern(destinationPattern.getValue(true));
        answer.setDestinationType(destinationPattern.getAttribute("destination-type", true));

        ElementParser splunkEventConfigurationElement = handledElementParser.getElement("splunk-event");
        if (splunkEventConfigurationElement != null) {
            ActiveMQMessageEventBuilderMetadata eventBuilderMetadata = new ActiveMQMessageEventBuilderMetadata();

            eventBuilderMetadata.setAttributes(splunkEventConfigurationElement.getAttributeValueMap(), true);

            List<ElementParser> systemPropertyElements = splunkEventConfigurationElement.getElements("system-property");
            if (systemPropertyElements != null && !systemPropertyElements.isEmpty()) {
                for (ElementParser systemPropertyElement : systemPropertyElements) {
                    String property = systemPropertyElement.getAttribute("property", true);
                    String field = systemPropertyElement.getAttribute("field");
                    eventBuilderMetadata.addSystemProperty(property, field);
                }
            }

            answer.setEventBuilderMetadata(eventBuilderMetadata);
        }

        return answer;
    }

    @Override
    protected SplunkNamespaceHandler getNamespaceHandler() {
        return (SplunkNamespaceHandler) super.getNamespaceHandler();
    }

}
