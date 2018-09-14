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
import com.pronoia.splunk.jms.eventbuilder.CamelJmsMessageEventBuilder;

import org.osgi.service.blueprint.reflect.Metadata;


public abstract class AbstractActiveMqMessageEventBuilderMetadata extends AbstractEventBuilderMetadata {
    protected AbstractActiveMqMessageEventBuilderMetadata(String className) {
        super(className);
    }

    protected AbstractActiveMqMessageEventBuilderMetadata(Class clazz) {
        super(clazz);
    }

    public AbstractActiveMqMessageEventBuilderMetadata() {
        super(CamelJmsMessageEventBuilder.class);
    }

    @Override
    public String translatePropertyName(String name) {
        String translatedPropertyName = null;

        switch (name) {
            case "include-jms-destination":
                translatedPropertyName = "includeJmsDestination";
                break;
            case "include-jms-delivery-mode":
                translatedPropertyName = "includeJmsDeliveryMode";
                break;
            case "include-jms-expiration":
                translatedPropertyName = "includeJmsExpiration";
                break;
            case "include-jms-priority":
                translatedPropertyName = "includeJmsPriority";
                break;
            case "include-jms-message-id":
                translatedPropertyName = "includeJmsMessageId";
                break;
            case "include-jms-timestamp":
                translatedPropertyName = "includeJmsTimestamp";
                break;
            case "include-jms-correlation-id":
                translatedPropertyName = "includeJmsCorrelationId";
                break;
            case "include-jms-reply-to":
                translatedPropertyName = "includeJmsReplyTo";
                break;
            case "include-jms-type":
                translatedPropertyName = "includeJmsType";
                break;
            case "include-jms-redelivered":
                translatedPropertyName = "includeJmsRedelivered";
                break;
            case "include-jms-properties":
                translatedPropertyName = "includeJmsProperties";
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
                propertyMetadata = ValueMetadataUtil.create(Boolean.class, propertyValue);
                break;
            default:
                propertyMetadata = super.createPropertyMetadata(propertyName, propertyValue);
        }

        return propertyMetadata;
    }

}
