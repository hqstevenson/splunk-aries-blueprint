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

import com.pronoia.aries.blueprint.util.parser.ElementParser;
import com.pronoia.splunk.aries.blueprint.metadata.SplunkEventCollectorClientServiceMetadata;
import com.pronoia.splunk.aries.blueprint.metadata.SplunkSimpleEventCollectorClientMetadata;
import com.pronoia.splunk.aries.blueprint.namespace.SplunkNamespaceHandler;

import java.util.Map;

import org.apache.aries.blueprint.ParserContext;
import org.osgi.service.blueprint.reflect.Metadata;


public class SplunkClientElementHandler extends AbstractSplunkElementHandler {
    public SplunkClientElementHandler(SplunkNamespaceHandler namespaceHandler, String elementTagName) {
        super(namespaceHandler, elementTagName);
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser, ParserContext parserContext) {
        SplunkSimpleEventCollectorClientMetadata splunkSimpleEventCollectorClientMetadata = new SplunkSimpleEventCollectorClientMetadata();

        Map<String, String> attributeValues = handledElementParser.getAttributeValueMap();
        splunkSimpleEventCollectorClientMetadata.addProperties(attributeValues, true);


        SplunkEventCollectorClientServiceMetadata splunkEventCollectorClientServiceMetadata = new SplunkEventCollectorClientServiceMetadata(splunkSimpleEventCollectorClientMetadata);
        log.debug("Registering metadata for Splunk Event Collector Client Service {}: {}", splunkEventCollectorClientServiceMetadata.getId(), splunkEventCollectorClientServiceMetadata);
        parserContext.getComponentDefinitionRegistry().registerComponentDefinition(splunkEventCollectorClientServiceMetadata);

        log.debug("Returning metadata for Splunk Event Collector Client {}: {}", splunkSimpleEventCollectorClientMetadata.getId(), splunkSimpleEventCollectorClientMetadata);
        return splunkSimpleEventCollectorClientMetadata;
    }

}
