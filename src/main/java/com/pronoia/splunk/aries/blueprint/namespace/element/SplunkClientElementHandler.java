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
package com.pronoia.splunk.aries.blueprint.namespace.element;

import java.util.Map;

import com.pronoia.aries.blueprint.util.namespace.AbstractElementHandler;
import com.pronoia.aries.blueprint.util.parser.ElementParser;

import com.pronoia.splunk.aries.blueprint.metadata.SplunkClientMetadata;
import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import org.osgi.service.blueprint.reflect.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplunkClientElementHandler extends AbstractElementHandler {
    Logger log = LoggerFactory.getLogger(this.getClass());

    public SplunkClientElementHandler(SplunkNamespaceHandler namespaceHandler, String elementTagName) {
        super(namespaceHandler, elementTagName);
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        SplunkClientMetadata answer = new SplunkClientMetadata();

        Map<String, String> attributeValues = handledElementParser.getAttributeValueMap();

        for (String attribute : attributeValues.keySet()) {
            String value = attributeValues.get(attribute);

            log.debug("Processing attribute {} = {}", attribute, value);

            switch (attribute) {
                case "id":
                    answer.setId(value);
                    break;
                case "host":
                    answer.setHost(value);
                    break;
                case "port":
                    answer.setPort(Integer.valueOf(value));
                    break;
                case "authorization-token":
                    answer.setAuthorizationToken(value);
                    break;
                case "validate-certificates":
                    answer.setValidateCertificates(Boolean.valueOf(value));
                    break;
                case "use-ssl":
                    answer.setUseSSL(Boolean.valueOf(value));
                    break;
                case "xmlns":
                    // Don't need to log this attribute as ignored
                    break;
                default:
                    log.warn("Ignoring unknown attribute {} = {}", attribute, value);
                    break;
            }
        }

        return answer;
    }

    @Override
    protected SplunkNamespaceHandler getNamespaceHandler() {
        return (SplunkNamespaceHandler) super.getNamespaceHandler();
    }
}
