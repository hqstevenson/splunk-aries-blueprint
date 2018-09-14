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

import com.pronoia.aries.blueprint.util.metadata.AbstractBeanMetadata;
import com.pronoia.aries.blueprint.util.reflect.MapMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;

import java.util.Map;

import org.osgi.service.blueprint.reflect.MapMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractEventBuilderMetadata extends AbstractBeanMetadata {
    final Logger log = LoggerFactory.getLogger(this.getClassName());

    public AbstractEventBuilderMetadata(String className) {
        super(className);
    }

    public AbstractEventBuilderMetadata(Class clazz) {
        super(clazz);
    }

    @Override
    public String translatePropertyName(String name) {
        String translatedPropertyName = null;

        switch (name) {
            case "index":
                translatedPropertyName = "defaultIndex";
                break;
            case "source":
                translatedPropertyName = "defaultSource";
                break;
            case "sourcetype":
                translatedPropertyName = "defaultSourcetype";
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
            case "defaultIndex":
            case "defaultSource":
            case "defaultSourcetype":
                propertyMetadata = ValueMetadataUtil.create(String.class, propertyValue);
                break;
            default:
                // TODO:  Make the message better - include more detail
                String message = String.format("createPropertyMetadata(propertyName[%s], propertyValue[%s]) - unsupported propertyName", propertyName, propertyValue);
                throw new IllegalArgumentException(message);
        }

        return propertyMetadata;
    }

    public void setSystemProperties(MapMetadata systemPropertiesMetadata) {
        if (systemPropertiesMetadata != null && !systemPropertiesMetadata.getEntries().isEmpty()) {
            this.addProperty("includedSystemProperties", systemPropertiesMetadata);
        }
    }

    public void setConstantFields(MapMetadata constantFieldsMetadata) {
        if (constantFieldsMetadata != null && !constantFieldsMetadata.getEntries().isEmpty()) {
            this.addProperty("constantFields", constantFieldsMetadata);
        }
    }
}
