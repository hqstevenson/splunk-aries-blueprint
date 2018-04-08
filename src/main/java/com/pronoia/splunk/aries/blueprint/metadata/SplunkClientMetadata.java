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

import com.pronoia.aries.blueprint.util.metadata.AbstractServiceMetadata;
import com.pronoia.aries.blueprint.util.reflect.PrototypeBeanMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;

import com.pronoia.splunk.eventcollector.EventCollectorClient;
import com.pronoia.splunk.eventcollector.client.SimpleEventCollectorClient;

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.osgi.service.blueprint.reflect.Target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplunkClientMetadata extends AbstractServiceMetadata {
    String host;
    Integer port;
    String authorizationToken;
    Boolean useSSL;
    Boolean validateCertificates;

    final Logger log = LoggerFactory.getLogger(this.getClass());

    public SplunkClientMetadata() {
        super();

        this.interfaces.add(EventCollectorClient.class.getName());
    }

    @Override
    public Target getServiceComponent() {
        MutableBeanMetadata splunkClientMetadata = PrototypeBeanMetadataUtil.create(SimpleEventCollectorClient.class);

        if (hasHost()) {
            splunkClientMetadata.addProperty("host", ValueMetadataUtil.create(host));
        }

        if (hasPort()) {
            splunkClientMetadata.addProperty("port", ValueMetadataUtil.create(port));
        }

        if (hasAuthorizationToken()) {
            splunkClientMetadata.addProperty("authorizationToken", ValueMetadataUtil.create(authorizationToken));
        }

        if (hasValidateCertificates()) {
            splunkClientMetadata.addProperty("validateCertificates", ValueMetadataUtil.create(validateCertificates));
        }

        if (hasUseSSL()) {
            splunkClientMetadata.addProperty("useSSL", ValueMetadataUtil.create(useSSL));
        }

        return splunkClientMetadata;
    }

    @Override
    public void setId(String id) {
        super.setId(id);
        serviceProperties.put("splunk-client-id", id);
    }

    public boolean hasHost() {
        return host != null && !host.isEmpty();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean hasPort() {
        return port != null && port > 0;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean hasAuthorizationToken() {
        return authorizationToken != null && !authorizationToken.isEmpty();
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public boolean hasUseSSL() {
        return useSSL != null;
    }

    public Boolean getUseSSL() {
        return useSSL;
    }

    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }

    public boolean hasValidateCertificates() {
        return validateCertificates != null;
    }

    public Boolean getValidateCertificates() {
        return validateCertificates;
    }

    public void setValidateCertificates(Boolean validateCertificates) {
        this.validateCertificates = validateCertificates;
    }

}
