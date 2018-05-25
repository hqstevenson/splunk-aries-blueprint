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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pronoia.aries.blueprint.util.metadata.AbstractSingletonBeanMetadata;
import com.pronoia.aries.blueprint.util.reflect.BeanPropertyMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ListMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ReferenceMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;

import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import com.pronoia.splunk.eventcollector.EventCollectorClient;
import com.pronoia.splunk.jmx.SplunkJmxAttributeChangeMonitor;

import org.apache.aries.blueprint.mutable.MutableReferenceMetadata;

import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplunkJmxAttributeChangeMonitorMetadata extends AbstractSingletonBeanMetadata {
    static final Map<String, String> ATTRIBUTE_TO_PROPERTY_MAP;

    static {
        ATTRIBUTE_TO_PROPERTY_MAP = new HashMap<>();
        ATTRIBUTE_TO_PROPERTY_MAP.put("id", null);
        ATTRIBUTE_TO_PROPERTY_MAP.put("splunk-client-id", "splunkClient"); // ref
        ATTRIBUTE_TO_PROPERTY_MAP.put("granularity-period", "granularityPeriod");
        ATTRIBUTE_TO_PROPERTY_MAP.put("max-suppressed-duplicate-events", "maxSuppressedDuplicates");
    }

    final SplunkNamespaceHandler namespaceHandler;
    final Logger log = LoggerFactory.getLogger(this.getClassName());

    List<String> observedObjects;
    List<String> observedAttributes;
    List<String> collectedAttributes;
    List<String> excludedAttributes;

    Metadata eventBuilderMetadata;

    public SplunkJmxAttributeChangeMonitorMetadata(SplunkNamespaceHandler namespaceHandler) {
        super(SplunkJmxAttributeChangeMonitor.class.getName());

        this.namespaceHandler = namespaceHandler;
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
        case "splunkClient":
            MutableReferenceMetadata referenceMetadata = ReferenceMetadataUtil.create(EventCollectorClient.class);
            if (propertyValue != null && !propertyName.isEmpty()) {
                referenceMetadata.setFilter("splunk-client-id=" + propertyValue);
            }
            answer = BeanPropertyMetadataUtil.create(propertyName, referenceMetadata);
            break;
        case "granularityPeriod":
        case "maxSuppressedDuplicates":
            answer = BeanPropertyMetadataUtil.create(propertyName, ValueMetadataUtil.create(Integer.class, propertyValue));
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

        answer.add(BeanPropertyMetadataUtil.create("observedObjects", ListMetadataUtil.create(observedObjects)));

        if (hasObservedAttributes()) {
            answer.add(BeanPropertyMetadataUtil.create("observedAttributes", ListMetadataUtil.create(observedAttributes)));
        }

        if (hasCollectedAttributes()) {
            answer.add(BeanPropertyMetadataUtil.create("collectedAttributes", ListMetadataUtil.create(collectedAttributes)));
        }

        if (hasExcludedAttributes()) {
            answer.add(BeanPropertyMetadataUtil.create("excludedAttributes", ListMetadataUtil.create(excludedAttributes)));
        }

        if (hasEventBuilderMetadata()) {
            answer.add(BeanPropertyMetadataUtil.create("splunkEventBuilder", eventBuilderMetadata));
        }

        return answer;
    }

    public void setObservedObjects(List<String> observedObjects) {
        this.observedObjects = observedObjects;
    }

    public boolean hasObservedAttributes() {
        return observedAttributes != null && !observedAttributes.isEmpty();
    }

    public void setObservedAttributes(List<String> observedAttributes) {
        this.observedAttributes = observedAttributes;
    }

    public List<String> getObservedAttributes() {
        List<String> answer = new LinkedList<>();

        if (observedAttributes != null && !observedAttributes.isEmpty()) {
            answer.addAll(observedAttributes);
        }

        return answer;
    }

    public boolean hasCollectedAttributes() {
        return collectedAttributes != null && !collectedAttributes.isEmpty();
    }

    public List<String> getCollectedAttributes() {
        List<String> answer = new LinkedList<>();

        if (collectedAttributes != null && !collectedAttributes.isEmpty()) {
            answer.addAll(collectedAttributes);
        }

        return answer;
    }

    public void setCollectedAttributes(List<String> collectedAttributes) {
        this.collectedAttributes = collectedAttributes;
    }

    public boolean hasExcludedAttributes() {
        return excludedAttributes != null && !excludedAttributes.isEmpty();
    }

    public List<String> getExcludedAttributes() {
        List<String> answer = new LinkedList<>();

        if (excludedAttributes != null && !excludedAttributes.isEmpty()) {
            answer.addAll(excludedAttributes);
        }

        return answer;
    }

    public void setExcludedAttributes(List<String> excludedAttributes) {
        this.excludedAttributes = excludedAttributes;
    }

    public boolean hasEventBuilderMetadata() {
        return eventBuilderMetadata != null;
    }

    public void setEventBuilderMetadata(Metadata eventBuilderMetadata) {
        this.eventBuilderMetadata = eventBuilderMetadata;
    }
}
