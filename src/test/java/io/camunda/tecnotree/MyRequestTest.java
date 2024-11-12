package io.camunda.tecnotree;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.error.ConnectorInputException;
import io.camunda.connector.test.outbound.OutboundConnectorContextBuilder;
import io.camunda.tecnotree.dto.Authentication;
import io.camunda.tecnotree.dto.MyConnectorRequest;
import org.junit.jupiter.api.Test;

public class MyRequestTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReplaceSecretsInAuthentication() throws Exception {
        // given
        var input = new MyConnectorRequest();
        input.setSoapEndpoint("http://example.com/soap");
        input.setSoapAction("urn:SomeAction");
        input.setSoapBody("<soapenv:Envelope>...</soapenv:Envelope>");
        input.setAuthentication(new Authentication("user", "secrets.PASSWORD"));

        var context = OutboundConnectorContextBuilder.create()
                .secret("PASSWORD", "actual_password")
                .variables(objectMapper.writeValueAsString(input))
                .build();

        // when
        var request = context.bindVariables(MyConnectorRequest.class);

        // then
        assertThat(request.getAuthentication().password())
                .isEqualTo("actual_password");
    }

    @Test
    void shouldFailValidationWhenAuthenticationIsMissing() throws Exception {
        // given
        var input = new MyConnectorRequest();
        input.setSoapEndpoint("http://example.com/soap");
        input.setSoapAction("urn:SomeAction");
        input.setSoapBody("<soapenv:Envelope>...</soapenv:Envelope>");
        // Authentication is missing

        var context = OutboundConnectorContextBuilder.create()
                .variables(objectMapper.writeValueAsString(input))
                .build();

        // when & then
        assertThatThrownBy(() -> context.bindVariables(MyConnectorRequest.class))
                .isInstanceOf(ConnectorInputException.class)
                .hasMessageContaining("authentication: Validation failed");
    }


    @Test
    void shouldFailValidationWhenSoapEndpointIsMissing() throws Exception {
        // given
        var input = new MyConnectorRequest();
        // SoapEndpoint is missing
        input.setSoapAction("urn:SomeAction");
        input.setSoapBody("<soapenv:Envelope>...</soapenv:Envelope>");
        input.setAuthentication(new Authentication("user", "pass"));

        var context = OutboundConnectorContextBuilder.create()
                .variables(objectMapper.writeValueAsString(input))
                .build();

        // when & then
        assertThatThrownBy(() -> context.bindVariables(MyConnectorRequest.class))
                .isInstanceOf(ConnectorInputException.class)
                .hasMessageContaining("soapEndpoint");
    }

    @Test
    void shouldFailValidationWhenPasswordIsMissing() throws Exception {
        // given
        var input = new MyConnectorRequest();
        input.setSoapEndpoint("http://example.com/soap");
        input.setSoapAction("urn:SomeAction");
        input.setSoapBody("<soapenv:Envelope>...</soapenv:Envelope>");
        input.setAuthentication(new Authentication("user", null));

        var context = OutboundConnectorContextBuilder.create()
                .variables(objectMapper.writeValueAsString(input))
                .build();

        // when & then
        assertThatThrownBy(() -> context.bindVariables(MyConnectorRequest.class))
                .isInstanceOf(ConnectorInputException.class)
                .hasMessageContaining("password");
    }

}
