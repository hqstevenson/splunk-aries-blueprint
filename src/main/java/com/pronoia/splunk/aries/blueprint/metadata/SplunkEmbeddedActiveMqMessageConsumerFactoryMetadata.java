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
package com.pronoia.splunk.aries.blueprint.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pronoia.aries.blueprint.util.metadata.AbstractSingletonBeanMetadata;
import com.pronoia.aries.blueprint.util.reflect.BeanPropertyMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ReferenceMetadataUtil;

import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;
import com.pronoia.splunk.eventcollector.EventCollectorClient;
import com.pronoia.splunk.jms.activemq.SplunkEmbeddedActiveMQMessageConsumerFactory;

import org.apache.aries.blueprint.mutable.MutableReferenceMetadata;

import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplunkEmbeddedActiveMqMessageConsumerFactoryMetadata extends AbstractSingletonBeanMetadata {
    static final Map<String, String> ATTRIBUTE_TO_PROPERTY_MAP;

    static {
        ATTRIBUTE_TO_PROPERTY_MAP = new HashMap<>();
        ATTRIBUTE_TO_PROPERTY_MAP.put("splunk-client-id", "splunkClient"); // ref
        ATTRIBUTE_TO_PROPERTY_MAP.put("jms-user-name", "userName"); // ref
        ATTRIBUTE_TO_PROPERTY_MAP.put("jms-password", "password"); // ref
    }

    final SplunkNamespaceHandler namespaceHandler;
    final Logger log = LoggerFactory.getLogger(this.getClassName());

    Metadata eventBuilderMetadata;
    String destinationType = "Queue";
    String destinationNamePattern = "splunk.*";


    public SplunkEmbeddedActiveMqMessageConsumerFactoryMetadata(SplunkNamespaceHandler namespaceHandler) {
        super(SplunkEmbeddedActiveMQMessageConsumerFactory.class.getName());

        this.namespaceHandler = namespaceHandler;
    }

    @Override
    public boolean usesAttribute(String attributeName) {
        return ATTRIBUTE_TO_PROPERTY_MAP.containsKey(attributeName);
    }

    @Override
    public String getPropertyName(String attributeName) {
        if (usesAttribute(attributeName)) {
            return ATTRIBUTE_TO_PROPERTY_MAP.get(attributeName);
        }

        // TODO:  Make the message better - include more detail
        throw new ComponentDefinitionException("Could not determine property name for attribute " + attributeName);
    }

    @Override
    public BeanProperty getPropertyMetadata(String propertyName, String propertyValue) {
        if (propertyName == null || propertyName.isEmpty()) {
            // TODO:  Make the message better - include more detail
            String message = String.format("getPropertyMetadata(propertyName[%s], propertyValue[%s]) - propertyName argument cannot be null or empty", propertyName, propertyValue);
            throw new IllegalArgumentException(message);
        }

        BeanProperty answer = null;

        switch (propertyName) {
            case "splunkClient":
                MutableReferenceMetadata referenceMetadata = ReferenceMetadataUtil.create(EventCollectorClient.class);
                if (propertyValue != null && !propertyName.isEmpty()) {
                    referenceMetadata.setFilter("splunk-client-id=" + propertyValue);
                }
                answer = BeanPropertyMetadataUtil.create(propertyName, referenceMetadata);
                break;
            case "userName":
            case "password":
                answer = BeanPropertyMetadataUtil.create(propertyName, propertyValue);
                break;
             default:
                // TODO:  Make the message better - include more detail
                String message = String.format("getPropertyMetadata(propertyName[%s], propertyValue[%s]) - unsupported propertyName", propertyName, propertyValue);
                throw new IllegalArgumentException(message);
        }

        return answer;
    }

    @Override
    public List<BeanProperty> getProperties() {
        List<BeanProperty> answer = super.getProperties();

        if (!hasAttribute("splunk-client-id")) {
            MutableReferenceMetadata referenceMetadata = ReferenceMetadataUtil.create(EventCollectorClient.class);
            if (namespaceHandler.hasDefaultSplunkClientId()) {
                referenceMetadata.setFilter("splunk-client-id=" + namespaceHandler.getDefaultSplunkClientId());
            }
            answer.add(BeanPropertyMetadataUtil.create("splunkClient", referenceMetadata));
        }

        if (hasDestinationType()) {
            answer.add(BeanPropertyMetadataUtil.create("destinationType", destinationType));
        }

        if (hasDestinationNamePattern()) {
            answer.add(BeanPropertyMetadataUtil.create("destinationNamePattern", destinationNamePattern));
        }

        if (hasEventBuilderMetadata()) {
            answer.add(BeanPropertyMetadataUtil.create("splunkEventBuilder", eventBuilderMetadata));
        }

        return answer;
    }

    public boolean hasDestinationType() {
        return destinationType != null && !destinationType.isEmpty();
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public boolean hasDestinationNamePattern() {
        return destinationNamePattern != null && !destinationNamePattern.isEmpty();
    }

    public String getDestinationNamePattern() {
        return destinationNamePattern;
    }

    public void setDestinationNamePattern(String destinationNamePattern) {
        this.destinationNamePattern = destinationNamePattern;
    }

    public boolean hasEventBuilderMetadata() {
        return eventBuilderMetadata != null;
    }

    public void setEventBuilderMetadata(Metadata eventBuilderMetadata) {
        this.eventBuilderMetadata = eventBuilderMetadata;
    }

}
