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
import com.pronoia.aries.blueprint.util.reflect.SetMetadataUtil;
import com.pronoia.splunk.aries.blueprint.metadata.AbstractActiveMqMessageEventBuilderMetadata;
import com.pronoia.splunk.aries.blueprint.metadata.ActiveMQAdvisoryMessageEventBuilderMetadata;
import com.pronoia.splunk.aries.blueprint.metadata.SplunkEmbeddedActiveMqConsumerFactoryMetadata;
import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.blueprint.reflect.Metadata;


public abstract class AbstractSplunkMessageConsumerFactoryElementHandler extends AbstractSplunkElementHandler {
    public AbstractSplunkMessageConsumerFactoryElementHandler(SplunkNamespaceHandler namespaceHandler, String elementTagName) {
        super(namespaceHandler, elementTagName);
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        SplunkEmbeddedActiveMqConsumerFactoryMetadata answer = new SplunkEmbeddedActiveMqConsumerFactoryMetadata();

        Map<String, String> attributeValues = handledElementParser.getAttributeValueMap();
        addDefaultSplunkClientId(attributeValues);
        answer.addProperties(attributeValues, true);

        ElementParser destinationPattern = handledElementParser.getElement("destination-pattern", true);
        answer.setDestinationNamePattern(destinationPattern.getValue(true));
        answer.setDestinationType(destinationPattern.getAttribute("destination-type", true));

        List<ElementParser> consumedHttpStatusCodes = handledElementParser.getElements("consumed-http-status-code");
        if (consumedHttpStatusCodes != null && !consumedHttpStatusCodes.isEmpty()) {
            Set<Integer> consumedHttpStatusCodeSet = new HashSet<>();
            for (ElementParser httpStatusCodeElement : consumedHttpStatusCodes) {
                String value = httpStatusCodeElement.getValue(true);
                consumedHttpStatusCodeSet.add(Integer.parseInt(value));
            }
            if (!consumedHttpStatusCodeSet.isEmpty()) {
                answer.addProperty("consumedHttpStatusCodes", SetMetadataUtil.create(consumedHttpStatusCodeSet));
            }
        }

        List<ElementParser> consumedSplunkStatusCodes = handledElementParser.getElements("consumed-splunk-status-code");
        if (consumedSplunkStatusCodes != null && !consumedSplunkStatusCodes.isEmpty()) {
            Set<Integer> consumedSplunkStatusCodeSet = new HashSet<>();
            for (ElementParser splunkStatusCodeElement : consumedSplunkStatusCodes) {
                String value = splunkStatusCodeElement.getValue(true);
                consumedSplunkStatusCodeSet.add(Integer.parseInt(value));
            }
            if (!consumedSplunkStatusCodeSet.isEmpty()) {
                answer.addProperty("consumedSplunkStatusCodes", SetMetadataUtil.create(consumedSplunkStatusCodeSet));
            }
        }

        ElementParser splunkEventConfigurationElement = handledElementParser.getElement("splunk-event");
        if (splunkEventConfigurationElement != null) {
            AbstractActiveMqMessageEventBuilderMetadata eventBuilderMetadata = createEventBuilderMetadata();

            eventBuilderMetadata.addProperties(splunkEventConfigurationElement.getAttributeValueMap(), true);

            eventBuilderMetadata.setConstantFields(parseConstantFields(splunkEventConfigurationElement));
            eventBuilderMetadata.setEnvironmentVariables(parseEnvironmentVariables(splunkEventConfigurationElement));
            eventBuilderMetadata.setSystemProperties(parseSystemProperties(splunkEventConfigurationElement));

            answer.setEventBuilderMetadata(eventBuilderMetadata);
        }

        log.debug("Returning metadata for Splunk Embedded ActiveMQ Advisory Message Consumer Factory: {}", answer);

        return answer;
    }

    abstract AbstractActiveMqMessageEventBuilderMetadata createEventBuilderMetadata();

}
