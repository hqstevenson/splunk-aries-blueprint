package com.pronoia.splunk.aries.blueprint.metadata;

import com.pronoia.aries.blueprint.util.metadata.AbstractServiceMetadata;
import com.pronoia.aries.blueprint.util.reflect.RefMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;
import com.pronoia.splunk.eventcollector.EventCollectorClient;

import java.util.Arrays;

import org.osgi.service.blueprint.reflect.BeanMetadata;


public class SplunkEventCollectorClientServiceMetadata extends AbstractServiceMetadata {

    public SplunkEventCollectorClientServiceMetadata(BeanMetadata splunkEventCollectorClientMetadata) {
        String splunkClientId = splunkEventCollectorClientMetadata.getId();

        setId(splunkClientId + "-service");
        setInterfaceNames(Arrays.asList(EventCollectorClient.class.getName()));
        setServiceComponent(RefMetadataUtil.create(splunkClientId));
        addServiceProperty(ValueMetadataUtil.create("splunk-client-id"), ValueMetadataUtil.create(splunkClientId));
    }
}
