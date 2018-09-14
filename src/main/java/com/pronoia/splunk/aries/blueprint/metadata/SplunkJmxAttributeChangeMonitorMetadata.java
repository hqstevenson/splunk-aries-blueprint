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

import com.pronoia.aries.blueprint.util.reflect.ListMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;
import com.pronoia.splunk.jmx.SplunkJmxAttributeChangeMonitor;

import java.util.List;

import org.osgi.service.blueprint.reflect.Metadata;


public class SplunkJmxAttributeChangeMonitorMetadata extends AbstractEventGeneratorMetadata {
    public SplunkJmxAttributeChangeMonitorMetadata() {
        super(SplunkJmxAttributeChangeMonitor.class);

    }

    @Override
    public String translatePropertyName(String name) {
        String translatedPropertyName = null;

        switch (name) {
            case "granularity-period":
                translatedPropertyName = "granularityPeriod";
                break;
            case "max-suppressed-duplicate-events":
                translatedPropertyName = "maxSuppressedDuplicates";
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
            case "granularityPeriod":
            case "maxSuppressedDuplicates":
                propertyMetadata = ValueMetadataUtil.create(Integer.class, propertyValue);
                break;
            default:
                propertyMetadata = super.createPropertyMetadata(propertyName, propertyValue);
        }

        return propertyMetadata;
    }

     public void setObservedObjects(List<String> observedObjects) {
        if (observedObjects != null && !observedObjects.isEmpty()) {
            this.addProperty("observedObjects", ListMetadataUtil.create(observedObjects));
        } else {
            throw new IllegalArgumentException("setObservedObjects(List<String>) - List cannot be null or empty");
        }
    }

    public void setObservedAttributes(List<String> observedAttributes) {
        if (observedAttributes != null && !observedAttributes.isEmpty()) {
            this.addProperty("observedAttributes", ListMetadataUtil.create(observedAttributes));
        }
    }

    public void setCollectedAttributes(List<String> collectedAttributes) {
        if (collectedAttributes != null && !collectedAttributes.isEmpty()) {
            this.addProperty("collectedAttributes", ListMetadataUtil.create(collectedAttributes));
        }
    }

    public void setExcludedAttributes(List<String> excludedAttributes) {
        if (excludedAttributes != null && !excludedAttributes.isEmpty()) {
            this.addProperty("excludedAttributes", ListMetadataUtil.create(excludedAttributes));
        }
    }
}
