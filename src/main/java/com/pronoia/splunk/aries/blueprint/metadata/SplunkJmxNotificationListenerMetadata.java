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
import com.pronoia.splunk.jmx.SplunkJmxNotificationListener;

import java.util.List;
import java.util.Map;


public class SplunkJmxNotificationListenerMetadata extends AbstractEventGeneratorMetadata {

    public SplunkJmxNotificationListenerMetadata() {
        super(SplunkJmxNotificationListener.class);
        setInitMethod("initialize");
        setDestroyMethod("destroy");
    }

    @Override
    public void addProperties(Map<String, String> properties, boolean logIgnoredProperties) {
        super.addProperties(properties, logIgnoredProperties);
        this.addProperty("notificationListenerId", ValueMetadataUtil.create(getId()));
    }


    public void setSourceMBeans(List<String> sourceMBeans) {
        if (sourceMBeans != null && !sourceMBeans.isEmpty()) {
            this.addProperty("sourceMBeans", ListMetadataUtil.create(sourceMBeans));
        } else {
            throw new IllegalArgumentException("setSourceMBeans(List<String>) - List cannot be null or empty");
        }
    }

}
