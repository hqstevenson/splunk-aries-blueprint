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

import org.osgi.service.blueprint.reflect.Metadata;


public abstract class AbstractJmxEventBuilderMetadata extends AbstractEventBuilderMetadata {
    public AbstractJmxEventBuilderMetadata(String className) {
        super(className);
    }

    public AbstractJmxEventBuilderMetadata(Class clazz) {
        super(clazz);
    }

    @Override
    public String translatePropertyName(String name) {
        String translatedPropertyName = null;

        switch (name) {
        case "include-null-attrs":
            translatedPropertyName = "includeNullAttributes";
            break;
        case "include-empty-attrs":
            translatedPropertyName = "includeEmptyAttributes";
            break;
        case "include-empty-lists":
            translatedPropertyName = "includeEmptyObjectNameLists";
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
        case "includeNullAttributes":
        case "includeEmptyAttributes":
        case "includeEmptyObjectNameLists":
            propertyMetadata = ValueMetadataUtil.create(Boolean.class, propertyValue);
            break;
        default:
            propertyMetadata = super.createPropertyMetadata(propertyName, propertyValue);
        }

        return propertyMetadata;
    }

}
