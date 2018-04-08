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

import com.pronoia.aries.blueprint.util.metadata.AbstractBeanMetadata;
import com.pronoia.aries.blueprint.util.reflect.BeanPropertyMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.MapMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;
import com.pronoia.splunk.jms.eventbuilder.CamelJmsMessageEventBuilder;

import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.BeanProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractActiveMqMessageEventBuilderMetadata extends AbstractBeanMetadata {
    static final Map<String, String> ATTRIBUTE_TO_PROPERTY_MAP;

    static {
        ATTRIBUTE_TO_PROPERTY_MAP = new HashMap<>();
        ATTRIBUTE_TO_PROPERTY_MAP.put("index", "defaultIndex");
        ATTRIBUTE_TO_PROPERTY_MAP.put("source", "defaultSource");
        ATTRIBUTE_TO_PROPERTY_MAP.put("sourcetype", "defaultSourcetype");

        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-destination", "includeJmsDestination");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-delivery-mode", "includeJmsDeliveryMode");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-expiration", "includeJmsExpiration");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-priority", "includeJmsPriority");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-message-id", "includeJmsMessageId");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-timestamp", "includeJmsTimestamp");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-correlation-id", "includeJmsCorrelationId");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-reply-to", "includeJmsReplyTo");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-type", "includeJmsType");
        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-redelivered", "includeJmsRedelivered");

        ATTRIBUTE_TO_PROPERTY_MAP.put("include-jms-properties", "includeJmsProperties");
    }

    final Logger log = LoggerFactory.getLogger(this.getClassName());

    Map<String, String> includedSystemProperties;

    protected AbstractActiveMqMessageEventBuilderMetadata(Class eventBuilderClass) {
        super(eventBuilderClass.getName());
    }

    public AbstractActiveMqMessageEventBuilderMetadata() {
        super(CamelJmsMessageEventBuilder.class.getName());
    }

    @Override
    public boolean usesAttribute(String attributeName) {
        return ATTRIBUTE_TO_PROPERTY_MAP.containsKey(attributeName);
    }

    @Override
    public String getPropertyName(String name) {
        if (usesAttribute(name)) {
            return ATTRIBUTE_TO_PROPERTY_MAP.get(name);
        }

        // TODO:  Make the message better - include more detail
        throw new ComponentDefinitionException("Could not determine property name for " + name);
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
            case "defaultIndex":
            case "defaultSource":
            case "defaultSourcetype":
                answer = BeanPropertyMetadataUtil.create(propertyName, ValueMetadataUtil.create(String.class, propertyValue));
                break;
            case "includeJmsDestination":
            case "includeJmsDeliveryMode":
            case "includeJmsExpiration":
            case "includeJmsPriority":
            case "includeJmsMessageId":
            case "includeJmsTimestamp":
            case "includeJmsCorrelationId":
            case "includeJmsReplyTo":
            case "includeJmsType":
            case "includeJmsRedelivered":
            case "includeJmsProperties":
                answer = BeanPropertyMetadataUtil.create(propertyName, ValueMetadataUtil.create(Boolean.class, propertyValue));
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

        if (hasIncludedSystemProperties()) {
            answer.add(BeanPropertyMetadataUtil.create("includedSystemProperties", MapMetadataUtil.create(includedSystemProperties)));
        }

        return answer;
    }


    public boolean hasIncludedSystemProperties() {
        return includedSystemProperties != null && !includedSystemProperties.isEmpty();
    }

    public void addSystemProperty(String property) {
        addSystemProperty(property, null);
    }

    public void addSystemProperty(String property, String field) {
        if (includedSystemProperties == null) {
            includedSystemProperties = new HashMap<>();
        }

        if (field == null || field.isEmpty()) {
            includedSystemProperties.put(property, property);
        } else {
            includedSystemProperties.put(property, field);
        }
    }
}
