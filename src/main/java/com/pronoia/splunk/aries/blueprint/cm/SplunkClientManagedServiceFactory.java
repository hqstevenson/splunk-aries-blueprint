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
package com.pronoia.splunk.aries.blueprint.cm;

import com.pronoia.splunk.eventcollector.EventCollectorClient;
import com.pronoia.splunk.eventcollector.client.SimpleEventCollectorClient;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplunkClientManagedServiceFactory implements ManagedServiceFactory {
    public static final String FACTORY_PID = "com.pronoia.splunk.client";

    // Required keys in PID
    public static final String SPLUNK_CLIENT_ID = "splunk-client-id";
    public static final String SPLUNK_AUTHORIZATION_TOKEN = "authorization-token";

    // Optional keys in PID
    public static final String SPLUNK_HOST = "host";  // defaults to 0.0.0.0
    public static final String SPLUNK_PORT = "port"; // defaults to 8088
    public static final String VALIDATE_CERTIFICATES = "validate-certificates"; // defaults to true
    public static final String USE_SSL = "use-ssl"; // defaults to true

    // Optional keys for default values of default fields in PID
    public static final String SPLUNK_EVENT_HOST = "event.host";
    public static final String SPLUNK_EVENT_INDEX = "event.index";
    public static final String SPLUNK_EVENT_SOURCE = "event.source";
    public static final String SPLUNK_EVENT_SOURCETYPE = "event.sourcetype";

    // Optional keys/key-prefixes) for custom indexed fields in PID
    public static final String SPLUNK_EVENT_CONSTANT_FIELD_PREFIX = "event.constant-field.";
    public static final String SPLUNK_EVENT_ENVIRONMENT_VARIABLE_PREFIX = "event.environment-variable.";
    public static final String SPLUNK_EVENT_SYSTEM_PROPERTY_PREFIX = "event.system-property.";

    public static final String SPLUNK_EVENT_ENVIRONMENT_VARIABLES = "event.environment-variables";
    public static final String SPLUNK_EVENT_SYSTEM_PROPERTIES = "event.system-properties";

    public static final String DEFAULT_SPLUNK_HOST = "0.0.0.0";
    public static final int DEFAULT_SPLUNK_PORT = 8088;

    Logger log = LoggerFactory.getLogger(this.getClass());
    BundleContext bundleContext;
    BlueprintContainer blueprintContainer;

    ServiceRegistration registration;
    ServiceTracker configAdminTracker;

    Map<String, ServiceRegistration<SimpleEventCollectorClient>> serviceRegistrationMap = Collections.synchronizedMap(new HashMap<>());
    Map<String, SimpleEventCollectorClient> clientMap = Collections.synchronizedMap(new HashMap<>());

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setBlueprintContainer(BlueprintContainer blueprintContainer) {
        this.blueprintContainer = blueprintContainer;
    }

    @Override
    public String getName() {
        return "Splunk Client Service Factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        log.debug("Updated called: pid={} properties={}", pid, properties);
        ServiceRegistration clientServiceRegistration = null;
        boolean existingPid = false;

        if (serviceRegistrationMap.containsKey(pid)) {
            existingPid = true;
            clientServiceRegistration = serviceRegistrationMap.get(pid);

            if (clientServiceRegistration != null) {
                clientServiceRegistration.unregister();
            }
            serviceRegistrationMap.remove(pid);
        }

        if (clientMap.containsKey(pid)) {
            SimpleEventCollectorClient simpleEventCollectorClient = clientMap.get(pid);
            if (!existingPid) {
                log.warn("Unregistered client found: PID={}", pid);
            }
            if (simpleEventCollectorClient != null) {
                simpleEventCollectorClient.destroy();
            }
            clientMap.remove(pid);
        }

        SimpleEventCollectorClient client = parseSplunkConnectionProperties(existingPid, pid, properties);

        if (client != null) {
            clientMap.put(pid, client);
            //Configuration was verified above - parse optional event configuration
            parseSplunkEventProperties(client, pid, properties);
            client.initialize();

            //Configuration was verified above, now create service.
            Properties metadata = new Properties();
            metadata.setProperty("splunk-client-id", client.getClientId());
            clientServiceRegistration = bundleContext.registerService(EventCollectorClient.class.getName(), client, (Dictionary) metadata);
            serviceRegistrationMap.put(pid, clientServiceRegistration);
        }
    }

    @Override
    public void deleted(String pid) {
        if (serviceRegistrationMap.containsKey(pid)) {
            ServiceRegistration clientServiceRegistration = serviceRegistrationMap.get(pid);

            if (clientServiceRegistration != null) {
                clientServiceRegistration.unregister();
            }
            serviceRegistrationMap.remove(pid);
        }

        if (clientMap.containsKey(pid)) {
            SimpleEventCollectorClient simpleEventCollectorClient = clientMap.get(pid);
            if (simpleEventCollectorClient != null) {
                simpleEventCollectorClient.destroy();
            }
            clientMap.remove(pid);
        }

        log.info("deleted " + pid);
    }

    // We wire the init method in the blueprint file
    public void start() {
        log.info("Starting " + this.getName());
        Dictionary servProps = new Properties();
        servProps.put(Constants.SERVICE_PID, FACTORY_PID);
        registration = bundleContext.registerService(ManagedServiceFactory.class.getName(), this, servProps);
        configAdminTracker = new ServiceTracker(bundleContext, ConfigurationAdmin.class.getName(), null);
        configAdminTracker.open();
        log.info("Started " + this.getName());
    }

    // We wire the destroy method in the blueprint file
    public void stop() {
        log.info("Destroying Splunk Client factory {}", FACTORY_PID);
        registration.unregister();
        configAdminTracker.close();

        for (String pid : clientMap.keySet()) {
            SimpleEventCollectorClient simpleEventCollectorClient = clientMap.get(pid);
            if (simpleEventCollectorClient != null) {
                simpleEventCollectorClient.destroy();
            }
            clientMap.remove(pid);
        }

        for (String pid : serviceRegistrationMap.keySet()) {
            ServiceRegistration<SimpleEventCollectorClient> simpleEventCollectorClientServiceRegistration = serviceRegistrationMap.get(pid);
            if (simpleEventCollectorClientServiceRegistration != null) {
                simpleEventCollectorClientServiceRegistration.unregister();
            }
            serviceRegistrationMap.remove(pid);
        }
    }

    SimpleEventCollectorClient parseSplunkConnectionProperties(boolean existingPid, String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        SimpleEventCollectorClient client = new SimpleEventCollectorClient();

        if (properties != null && !properties.isEmpty()) {
            parseRequiredSplunkConnectionProperties(client, existingPid, pid, properties);
            parseOptionalSplunkConnectionProperties(client, existingPid, pid, properties);
        }

        return client;
    }

    void parseRequiredSplunkConnectionProperties(SimpleEventCollectorClient client, boolean existingPid, String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        String splunkClientId = (String) properties.get(SPLUNK_CLIENT_ID);
        if (splunkClientId != null && !splunkClientId.isEmpty()) {
            log.debug("PID={} {}={}", pid, SPLUNK_CLIENT_ID, splunkClientId);
            client.setClientId(splunkClientId);
        } else if (existingPid) {
            throw new ConfigurationException(SPLUNK_CLIENT_ID, String.format("Missing required key: PID=%s properties=%s", pid, properties));
        } else {
            log.info("Ignoring PID - {} not found: PID={} properties={}", SPLUNK_CLIENT_ID, pid, properties);
        }

        String authToken = (String) properties.get(SPLUNK_AUTHORIZATION_TOKEN);
        if (authToken != null && !authToken.isEmpty()) {
            log.debug("PID={} {}={}", pid, SPLUNK_AUTHORIZATION_TOKEN, authToken);
            client.setAuthorizationToken(authToken);
        } else {
            throw new ConfigurationException(SPLUNK_AUTHORIZATION_TOKEN, String.format("Missing required key: PID=%s properties=%s", pid, properties));
        }
    }

    void parseOptionalSplunkConnectionProperties(SimpleEventCollectorClient client, boolean existingPid, String pid, Dictionary<String, ?> properties) {
        String host = (String) properties.get(SPLUNK_HOST);
        if (host != null && !host.isEmpty()) {
            if (host.isEmpty()) {
                log.warn("Empty key - using default value={}: PID={} key={} properties={}", DEFAULT_SPLUNK_HOST, pid, SPLUNK_PORT, properties);
                client.setHost(DEFAULT_SPLUNK_HOST);
            } else {
                log.debug("PID={} {}={}", pid, SPLUNK_HOST, host);
                client.setHost(host);
            }
        } else {
            log.debug("Key not specified - using default value={}: PID={} key={} properties={}", DEFAULT_SPLUNK_HOST, pid, SPLUNK_PORT, properties);
            client.setHost(DEFAULT_SPLUNK_HOST);
        }

        String portString = (String) properties.get(SPLUNK_PORT);
        if (portString != null) {
            if (host.isEmpty()) {
                log.warn("Empty key - using default value={}: PID={} key={} properties={}", DEFAULT_SPLUNK_PORT, pid, SPLUNK_PORT, properties);
                client.setPort(DEFAULT_SPLUNK_PORT);
            } else {
                try {
                    Integer port = Integer.parseInt(portString);
                    client.setPort(port);
                } catch (NumberFormatException numberFormatEx) {
                    log.warn("Invalid key value - using default value={}: PID={} key={} value={}", DEFAULT_SPLUNK_PORT, pid, SPLUNK_PORT, portString);
                }
            }
        } else {
            log.debug("Key not specified - using default value={}: PID={} key={} properties={}", 8088, pid, SPLUNK_PORT, properties);
            client.setPort(8088);
        }

        String useSSLString = (String) properties.get(USE_SSL);
        if (useSSLString != null) {
            if (useSSLString.isEmpty()) {
                log.warn("Empty key - using default value={}: PID={} key={} properties={}", client.isUseSSL(), pid, USE_SSL, properties);
            } else {
                try {
                    Boolean useSSL = Boolean.parseBoolean(useSSLString);
                    client.setUseSSL(useSSL);
                } catch (Exception boolEx) {
                    log.warn("Invalid key value - using default value={}: PID={} key={} value={}", client.isUseSSL(), pid, USE_SSL, useSSLString);
                }
            }
        }

        String validateCertificatesString = (String) properties.get(VALIDATE_CERTIFICATES);
        if (validateCertificatesString != null) {
            if (validateCertificatesString.isEmpty()) {
                log.warn("Empty key - using default value={}: PID={} key={} properties={}", client.isCertificateValidationEnabled(), pid, VALIDATE_CERTIFICATES, properties);
            } else {
                try {
                    Boolean validateCertificates = Boolean.parseBoolean(validateCertificatesString);
                    client.setValidateCertificates(validateCertificates);
                } catch (Exception boolEx) {
                    log.warn("Invalid key value - using default value={}: PID={} key={} value={}", client.isCertificateValidationEnabled(), pid, VALIDATE_CERTIFICATES, validateCertificatesString);
                }
            }
        }
    }

    void parseSplunkEventProperties(SimpleEventCollectorClient client, String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties != null && !properties.isEmpty()) {
            parseSplunkEventDefaultFieldProperties(client, pid, properties);
            parseSplunkEventConstantFieldProperties(client, pid, properties);
            parseSplunkEventSystemPropertyFieldProperties(client, pid, properties);
            parseSplunkEventEnvironmentVariableFieldProperties(client, pid, properties);
        }
    }

    void parseSplunkEventDefaultFieldProperties(SimpleEventCollectorClient client, String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        String eventHost = (String) properties.get(SPLUNK_EVENT_HOST);
        if (eventHost != null) {
            eventHost = eventHost.trim();
            if (eventHost.isEmpty()) {
                log.warn("Ignoring empty key: PID={} key={} properties={}", pid, SPLUNK_EVENT_HOST, properties);
            } else {
                log.debug("PID={} {}={}", pid, SPLUNK_EVENT_HOST, eventHost);
                client.setEventHost(eventHost);
            }
        }

        String eventIndex = (String) properties.get(SPLUNK_EVENT_INDEX);
        if (eventIndex != null) {
            eventIndex = eventIndex.trim();
            if (eventIndex.isEmpty()) {
                log.warn("Ignoring empty key: PID={} key={} properties={}", pid, SPLUNK_EVENT_INDEX, properties);
            } else {
                log.debug("PID={} {}={}", pid, SPLUNK_EVENT_INDEX, eventIndex);
                client.setEventIndex(eventIndex);
            }
        }

        String eventSource = (String) properties.get(SPLUNK_EVENT_SOURCE);
        if (eventSource != null) {
            eventSource = eventSource.trim();
            if (eventSource.isEmpty()) {
                log.warn("Ignoring empty key: PID={} key={} properties={}", pid, SPLUNK_EVENT_SOURCE, properties);
            } else {
                log.debug("PID={} {}={}", pid, SPLUNK_EVENT_SOURCE, eventSource);
                client.setEventSource(eventSource);
            }
        }

        String eventSourcetype = (String) properties.get(SPLUNK_EVENT_SOURCETYPE);
        if (eventSourcetype != null) {
            eventSourcetype = eventSourcetype.trim();
            if (eventSourcetype.isEmpty()) {
                log.warn("Ignoring empty key: PID={} key={} properties={}", pid, SPLUNK_EVENT_SOURCETYPE, properties);
            } else {
                log.debug("PID={} {}={}", pid, SPLUNK_EVENT_SOURCETYPE, eventSourcetype);
                client.setEventSourcetype(eventSourcetype);
            }
        }
    }

    void parseSplunkEventConstantFieldProperties(SimpleEventCollectorClient client, String pid, Dictionary<String,?> properties) {
        Enumeration<String> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.startsWith(SPLUNK_EVENT_CONSTANT_FIELD_PREFIX)) {
                String[] keySegments = key.split("\\.");
                if (keySegments.length == 3) {
                    String value = getPropertyValue(pid, key, properties);
                    if (value != null ) {
                        client.addConstantField(keySegments[2], value);
                    }
                } else {
                    log.warn("Unable to determine constant event field name from key: PID={} key={} properties={}", pid, key, properties);
                }
            }
        }
    }


    void parseSplunkEventSystemPropertyFieldProperties(SimpleEventCollectorClient client, String pid, Dictionary<String,?> properties) {
        Object systemPropertyList = properties.get(SPLUNK_EVENT_SYSTEM_PROPERTIES);
        if (systemPropertyList != null) {
            String systemPropertyListString = systemPropertyList.toString().trim();
            if (!systemPropertyListString.isEmpty()) {
                String[] systemProperties = systemPropertyListString.split(",");
                for (String systemProperty : systemProperties) {
                    client.includeSystemProperty(systemProperty);
                }
            } else {
                log.warn("Ignoring empty value for key: PID={} key={} properties={}", pid, SPLUNK_EVENT_SYSTEM_PROPERTIES, properties);
            }
        }

        Enumeration<String> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.startsWith(SPLUNK_EVENT_SYSTEM_PROPERTY_PREFIX)) {
                String[] keySegments = key.split("\\.");
                if (keySegments.length == 3) {
                    String value = getPropertyValue(pid, key, properties);
                    if (value != null ) {
                        client.includeSystemProperty(value, keySegments[2]);
                    }
                } else {
                    log.warn("Unable to determine system property event field name from key: PID={} key={} properties={}", pid, key, properties);
                }
            }
        }
    }

    void parseSplunkEventEnvironmentVariableFieldProperties(SimpleEventCollectorClient client, String pid, Dictionary<String,?> properties) {
        Object environmentVariableList = properties.get(SPLUNK_EVENT_ENVIRONMENT_VARIABLES);
        if (environmentVariableList != null) {
            String environmentVariableListString = environmentVariableList.toString().trim();
            if (!environmentVariableListString.isEmpty()) {
                String[] environmentVariables = environmentVariableListString.split(",");
                for (String environmentVariable : environmentVariables) {
                    client.includeEnvironmentVariable(environmentVariable);
                }
            } else {
                log.warn("Ignoring empty value for key: PID={} key={} properties={}", pid, SPLUNK_EVENT_ENVIRONMENT_VARIABLES, properties);
            }
        }

        Enumeration<String> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.startsWith(SPLUNK_EVENT_ENVIRONMENT_VARIABLE_PREFIX)) {
                String[] keySegments = key.split("\\.");
                if (keySegments.length == 3) {
                    String value = getPropertyValue(pid, key, properties);
                    if (value != null ) {
                        client.includeEnvironmentVariable(value, keySegments[2]);
                    }
                } else {
                    log.warn("Unable to determine environment variable field name from key: PID={} key={} properties={}", pid, key, properties);
                }
            }
        }
    }

    String getPropertyValue(String pid, String key, Dictionary<String,?> properties) {
        String answer = null;
        Object value = properties.get(key);
        if (value != null ) {
            String stringValue = value.toString().trim();
            if (stringValue.isEmpty()) {
                log.warn("Ignoring empty value for key: PID={} key={} properties={}", pid, key, properties);
            } else {
                answer = stringValue;
            }
        } else {
            log.warn("Ignoring null value for key: pid={} PID={} properties={}", pid, key, properties);
        }

        return answer;
    }
}
