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

import com.pronoia.aries.blueprint.util.metadata.AbstractSingletonBeanMetadata;
import com.pronoia.aries.blueprint.util.reflect.ListMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ReferenceMetadataUtil;
import com.pronoia.splunk.eventcollector.EventCollectorClient;
import com.pronoia.splunk.jmx.SplunkJmxNotificationListener;

import java.util.List;

import org.osgi.service.blueprint.reflect.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractEventGeneratorMetadata extends AbstractSingletonBeanMetadata {
    public AbstractEventGeneratorMetadata(Class clazz) {
        super(clazz);

        setActivation(ACTIVATION_EAGER);
    }

    @Override
    public String translatePropertyName(String name) {
        String translatedPropertyName = null;

        switch (name) {
        case "splunk-client-id":
            translatedPropertyName = "splunkClient";
            break;
        default:
            // TODO:  Make the message better - include more detail
            log.debug("Unsupported name {} - returning null", name);
            break;
        }

        return translatedPropertyName;
    }

    @Override
    public Metadata createPropertyMetadata(String propertyName, String propertyValue) {
        if (propertyName == null || propertyName.isEmpty()) {
            // TODO:  Make the message better - include more detail
            String message = String.format("createPropertyMetadata(propertyName[%s], propertyValue[%s]) - propertyName argument cannot be null or empty", propertyName, propertyValue);
            throw new IllegalArgumentException(message);
        }

        Metadata propertyMetadata = null;

        switch (propertyName) {
            case "splunkClient":
                propertyMetadata = ReferenceMetadataUtil.create(EventCollectorClient.class, "splunk-client-id=" + propertyValue);
                break;
            default:
                // TODO:  Make the message better - include more detail
                String message = String.format("createPropertyMetadata(propertyName[%s], propertyValue[%s]) - unsupported propertyName", propertyName, propertyValue);
                throw new IllegalArgumentException(message);
        }

        return propertyMetadata;
    }

    public void setEventBuilderMetadata(Metadata eventBuilderMetadata) {
        if (eventBuilderMetadata != null) {
            this.addProperty("splunkEventBuilder", eventBuilderMetadata);
        } else {
            throw new IllegalArgumentException("setEventBuilderMetadata(Metadata) - Metadata cannot be null");
        }
    }

}
