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
import com.pronoia.aries.blueprint.util.reflect.ListMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.MapMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;

import com.pronoia.splunk.jmx.eventcollector.eventbuilder.JmxAttributeListEventBuilder;

import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.BeanProperty;

import org.osgi.service.blueprint.reflect.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmxAttributeListEventBuilderMetadata extends AbstractJmxEventBuilderMetadata {
    public JmxAttributeListEventBuilderMetadata() {
        super(JmxAttributeListEventBuilder.class.getName());
    }

    @Override
    public String translatePropertyName(String name) {
        String translatedPropertyName = null;

        switch (name) {
            case "include-zero-attrs":
                translatedPropertyName = "includeZeroAttributes";
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
            case "includeZeroAttributes":
                propertyMetadata = ValueMetadataUtil.create(Boolean.class, propertyValue);
                break;
            default:
                propertyMetadata = super.createPropertyMetadata(propertyName, propertyValue);
        }

        return propertyMetadata;
    }

    public void setCollectedAttributes(List<String> collectedAttributes) {
        if (collectedAttributes != null && !collectedAttributes.isEmpty()) {
            this.addProperty("collectedAttributes", ListMetadataUtil.create(collectedAttributes));
        }
    }

}
