package io.camunda.tecnotree.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import io.camunda.connector.generator.java.annotation.TemplateProperty;
import jakarta.validation.constraints.NotNull;

public class MyConnectorRequest {
    @Valid
    @NotNull
    private Authentication authentication;
    @NotEmpty
    @TemplateProperty(group = "request", label = "SOAP Endpoint", description = "The SOAP service endpoint URL")
    private String soapEndpoint;
    @NotEmpty
    @TemplateProperty(group = "request", label = "SOAP Action", description = "The SOAP action to invoke")
    private String soapAction;
    @NotEmpty
    @TemplateProperty(group = "request", label = "SOAP Body", description = "The SOAP request body")
    private String soapBody;

    // Getters and Setters

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public String getSoapEndpoint() {
        return soapEndpoint;
    }

    public void setSoapEndpoint(String soapEndpoint) {
        this.soapEndpoint = soapEndpoint;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getSoapBody() {
        return soapBody;
    }

    public void setSoapBody(String soapBody) {
        this.soapBody = soapBody;
    }
}
