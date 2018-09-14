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

import com.pronoia.aries.blueprint.util.parser.ElementParser;
import com.pronoia.splunk.aries.blueprint.metadata.ActiveMQMessageEventBuilderMetadata;
import com.pronoia.splunk.aries.blueprint.metadata.SplunkEmbeddedActiveMqMessageConsumerFactoryMetadata;
import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import java.util.Map;

import org.osgi.service.blueprint.reflect.Metadata;


public class SplunkEmbeddedActiveMqMessageConsumerFactoryElementHandler extends AbstractSplunkElementHandler {
    public SplunkEmbeddedActiveMqMessageConsumerFactoryElementHandler(SplunkNamespaceHandler namespaceHandler, String elementTagName) {
        super(namespaceHandler, elementTagName);
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        SplunkEmbeddedActiveMqMessageConsumerFactoryMetadata answer = new SplunkEmbeddedActiveMqMessageConsumerFactoryMetadata();

        Map<String, String> attributeValues = handledElementParser.getAttributeValueMap();
        addDefaultSplunkClientId(attributeValues);
        answer.addProperties(attributeValues, true);

        ElementParser destinationPattern = handledElementParser.getElement("destination-pattern", true);
        answer.setDestinationNamePattern(destinationPattern.getValue(true));
        answer.setDestinationType(destinationPattern.getAttribute("destination-type", true));

        ElementParser splunkEventConfigurationElement = handledElementParser.getElement("splunk-event");
        if (splunkEventConfigurationElement != null) {
            ActiveMQMessageEventBuilderMetadata eventBuilderMetadata = new ActiveMQMessageEventBuilderMetadata();

            eventBuilderMetadata.addProperties(splunkEventConfigurationElement.getAttributeValueMap(), true);

            eventBuilderMetadata.setConstantFields(parseConstantFields(splunkEventConfigurationElement));
            eventBuilderMetadata.setSystemProperties(parseSystemProperties(splunkEventConfigurationElement));

            answer.setEventBuilderMetadata(eventBuilderMetadata);
        }

        log.debug("Returning metadata for Splunk Embedded ActiveMQ Message Consumer Factory: {}", answer);

        return answer;
    }

}
