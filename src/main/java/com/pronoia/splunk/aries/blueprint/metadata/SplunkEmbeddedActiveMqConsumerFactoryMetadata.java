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

import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;
import com.pronoia.splunk.jms.activemq.SplunkEmbeddedActiveMQConsumerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.blueprint.reflect.MapMetadata;
import org.osgi.service.blueprint.reflect.Metadata;


public class SplunkEmbeddedActiveMqConsumerFactoryMetadata extends AbstractEventGeneratorMetadata {
    static final Map<String, String> ATTRIBUTE_TO_PROPERTY_MAP;

    static {
        ATTRIBUTE_TO_PROPERTY_MAP = new HashMap<>();
        ATTRIBUTE_TO_PROPERTY_MAP.put("jms-user-name", "userName"); // ref
        ATTRIBUTE_TO_PROPERTY_MAP.put("jms-password", "password"); // ref
    }

    Set<Integer> consumedHttpStatusCodes;
    Set<Integer> consumedSplunkStatusCodes;

    public SplunkEmbeddedActiveMqConsumerFactoryMetadata() {
        super(SplunkEmbeddedActiveMQConsumerFactory.class);
        setInitMethod("initialize");
        setDestroyMethod("destroy");
    }

    @Override
    public void addProperties(Map<String, String> properties, boolean logIgnoredProperties) {
        super.addProperties(properties, logIgnoredProperties);
        this.addProperty("consumerFactoryId", ValueMetadataUtil.create(getId()));
    }


    @Override
    public String translatePropertyName(String name) {
        String translatedPropertyName = null;

        switch (name) {
            case "delay":
                translatedPropertyName = "delaySeconds";
                break;
            case "initial-delay":
                translatedPropertyName = "initialDelaySeconds";
                break;
            case "receive-timeout":
                translatedPropertyName = "receiveTimeoutMillis";
                break;
            case "jms-user-name":
                translatedPropertyName = "userName";
                break;
            case "jms-password":
                translatedPropertyName = "password";
                break;
            case "host":
                translatedPropertyName = "eventHost";
                break;
            case "index":
                translatedPropertyName = "eventIndex";
                break;
            case "source":
                translatedPropertyName = "eventSource";
                break;
            case "sourcetype":
                translatedPropertyName = "eventSourcetype";
                break;
            default:
                translatedPropertyName = super.translatePropertyName(name);
                break;
        }

        return translatedPropertyName;
    }

    @Override
    public Metadata createPropertyMetadata(String propertyName, String propertyValue) {
        Metadata propertyMetadata = null;

        switch (propertyName) {
            case "userName":
            case "password":
            case "eventHost":
            case "eventIndex":
            case "eventSource":
            case "eventSourcetype":
                propertyMetadata = ValueMetadataUtil.create(String.class, propertyValue);
                break;
            case "delaySeconds":
            case "initialDelaySeconds":
            case "receiveTimeoutMillis":
                propertyMetadata = ValueMetadataUtil.create(Long.class, propertyValue);
                break;
            default:
                propertyMetadata = super.createPropertyMetadata(propertyName, propertyValue);
        }

        return propertyMetadata;
    }

    public void setDestinationType(String destinationType) {
        if (destinationType != null && !destinationType.isEmpty()) {
            addProperty("destinationType", ValueMetadataUtil.create(String.class, destinationType));
        }
    }

    public void setDestinationNamePattern(String destinationNamePattern) {
        if (destinationNamePattern != null && !destinationNamePattern.isEmpty()) {
            addProperty("destinationNamePattern", ValueMetadataUtil.create(String.class, destinationNamePattern));
        }
    }
}
