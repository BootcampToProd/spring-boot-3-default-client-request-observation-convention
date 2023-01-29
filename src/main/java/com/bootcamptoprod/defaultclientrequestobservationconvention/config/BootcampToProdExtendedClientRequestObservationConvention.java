package com.bootcamptoprod.defaultclientrequestobservationconvention.config;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The type Bootcamp to prod extended client request observation convention.
 * Useful for adding our own additional tags in rest template metrics
 */
@Component
public class BootcampToProdExtendedClientRequestObservationConvention extends DefaultClientRequestObservationConvention {

    // Use constructor only if you want to customize the name of metrics
    // Default name will be 'http.client.requests'
    /*public BootcampToProdExtendedClientRequestObservationConvention(@Value("${management.observations.http.client.requests.name}") String httpClientRequestsName) {
        super(httpClientRequestsName);
    }*/

    @Override
    public KeyValues getLowCardinalityKeyValues(ClientRequestObservationContext context) {
        // here, we just want to have an additional KeyValue to the observation, keeping the default values
        return super.getLowCardinalityKeyValues(context).and(additionalTags(context));
    }

    protected KeyValues additionalTags(ClientRequestObservationContext context) {
        KeyValues keyValues = KeyValues.empty();

        ClientHttpRequest request = context.getCarrier();
        String uri = request.getURI().toString();

        // Optional tag which will be present in metrics only when the condition is evaluated to true
        MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(uri).build().getQueryParams();

        if (parameters.containsKey("id")) {
            keyValues = keyValues.and(KeyValue.of("userId", parameters.get("id").get(0)));
        }

        // Custom tag which will be present in all the controller metrics
        keyValues = keyValues.and(KeyValue.of("tag", "value"));

        return keyValues;
    }


}
