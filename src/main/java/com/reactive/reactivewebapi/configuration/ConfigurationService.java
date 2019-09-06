package com.reactive.reactivewebapi.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "services")
public class ConfigurationService {
    private String itemEndPoint;
    private String shippingEndPoint;
    private String invoiceEndPoint;
}
