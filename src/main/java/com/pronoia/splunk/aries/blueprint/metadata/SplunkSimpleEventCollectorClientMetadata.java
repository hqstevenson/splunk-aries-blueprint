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
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;
import com.pronoia.splunk.eventcollector.client.SimpleEventCollectorClient;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.blueprint.reflect.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplunkSimpleEventCollectorClientMetadata extends AbstractSingletonBeanMetadata {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    public SplunkSimpleEventCollectorClientMetadata() {
        super(SimpleEventCollectorClient.class);
    }

    @Override
    public String translatePropertyName(String name) {
        String translatedPropertyName = null;

        switch (name) {
            case "host":
            case "port":
                translatedPropertyName = name;
                break;
            case "authorization-token":
                translatedPropertyName = "authorizationToken";
                break;
            case "validate-certificates":
                translatedPropertyName = "validateCertificates";
                break;
            case "use-ssl":
                translatedPropertyName = "useSSL";
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
            case "host":
            case "authorizationToken":
                propertyMetadata = ValueMetadataUtil.create(String.class, propertyValue);
                break;
            case "port":
                propertyMetadata = ValueMetadataUtil.create(Integer.class, propertyValue);
                break;
            case "validateCertificates":
            case "useSSL":
                propertyMetadata = ValueMetadataUtil.create(Boolean.class, propertyValue);
                break;
            default:
                // TODO:  Make the message better - include more detail
                String message = String.format("createPropertyMetadata(propertyName[%s], propertyValue[%s]) - unsupported propertyName", propertyName, propertyValue);
                throw new IllegalArgumentException(message);
        }

        return propertyMetadata;
    }
}
