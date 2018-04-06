/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import com.pronoia.aries.blueprint.util.reflect.ListMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ReferenceMetadataUtil;

import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import com.pronoia.splunk.eventcollector.EventCollectorClient;
import com.pronoia.splunk.jmx.SplunkJmxNotificationListener;

import org.apache.aries.blueprint.mutable.MutableReferenceMetadata;

import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplunkJmxNotificationListenerMetadata extends AbstractSingletonBeanMetadata {
    static final Map<String, String> ATTRIBUTE_TO_PROPERTY_MAP;

    static {
        ATTRIBUTE_TO_PROPERTY_MAP = new HashMap<>();
        ATTRIBUTE_TO_PROPERTY_MAP.put("splunk-client", "splunkClient"); // ref
    }

    final SplunkNamespaceHandler namespaceHandler;
    final Logger log = LoggerFactory.getLogger(this.getClassName());
    List<String> sourceMBeans;

    Metadata eventBuilderMetadata;

    public SplunkJmxNotificationListenerMetadata(SplunkNamespaceHandler namespaceHandler) {
        super(SplunkJmxNotificationListener.class.getName());

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

        if (!hasAttribute("splunk-client")) {
            MutableReferenceMetadata referenceMetadata = ReferenceMetadataUtil.create(EventCollectorClient.class);
            if (namespaceHandler.hasDefaultSplunkClientId()) {
                referenceMetadata.setFilter("splunk-client-id=" + namespaceHandler.getDefaultSplunkClientId());
            }
            answer.add(BeanPropertyMetadataUtil.create("splunkClient", referenceMetadata));
        }

        if (sourceMBeans != null && !sourceMBeans.isEmpty()) {
            answer.add(BeanPropertyMetadataUtil.create("sourceMBeans", ListMetadataUtil.create(sourceMBeans)));
        }

        if (hasEventBuilderMetadata()) {
            answer.add(BeanPropertyMetadataUtil.create("splunkEventBuilder", eventBuilderMetadata));
        }

        return answer;
    }

    public List<String> getSourceMBeans() {
        return sourceMBeans;
    }

    public void setSourceMBeans(List<String> sourceMBeans) {
        this.sourceMBeans = sourceMBeans;
    }

    public boolean hasEventBuilderMetadata() {
        return eventBuilderMetadata != null;
    }

    public void setEventBuilderMetadata(Metadata eventBuilderMetadata) {
        this.eventBuilderMetadata = eventBuilderMetadata;
    }

}
