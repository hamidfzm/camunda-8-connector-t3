package io.camunda.tecnotree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.test.outbound.OutboundConnectorContextBuilder;
import io.camunda.tecnotree.dto.Authentication;
import io.camunda.tecnotree.dto.MyConnectorRequest;
import io.camunda.tecnotree.dto.MyConnectorResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFunctionTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldReturnSOAPResponseWhenExecuteSuccessfully() throws Exception {
    // given
    var input = new MyConnectorRequest();
    input.setSoapEndpoint("http://example.com/soap");
    input.setSoapAction("urn:SomeAction");
    input.setSoapBody("<soapenv:Envelope>...</soapenv:Envelope>");
    input.setAuthentication(new Authentication("user", "pass"));

    var function = Mockito.spy(new MyConnectorFunction());
    var context = OutboundConnectorContextBuilder.create()
            .variables(objectMapper.writeValueAsString(input))
            .build();

    // Mock the executeConnector method to avoid actual HTTP calls
    Mockito.doReturn(new MyConnectorResult("<SOAP Response>"))
            .when(function).executeConnector(Mockito.any());

    // when
    var result = function.execute(context);

    // then
    assertThat(result)
            .isInstanceOf(MyConnectorResult.class)
            .extracting("response")
            .isEqualTo("<SOAP Response>");
  }

  @Test
  void shouldThrowConnectorExceptionWhenSOAPFaultOccurs() throws Exception {
    // given
    var input = new MyConnectorRequest();
    input.setSoapEndpoint("http://invalid-endpoint");
    input.setSoapAction("urn:InvalidAction");
    input.setSoapBody("<soapenv:Envelope>...</soapenv:Envelope>");
    input.setAuthentication(new Authentication("user", "pass"));

    var function = Mockito.spy(new MyConnectorFunction());
    var context = OutboundConnectorContextBuilder.create()
            .variables(objectMapper.writeValueAsString(input))
            .build();

    // Mock the executeConnector method to throw an exception
    Mockito.doThrow(new ConnectorException("SOAP_ERROR", "Simulated SOAP fault"))
            .when(function).executeConnector(Mockito.any());

    // when
    var throwable = catchThrowable(() -> function.execute(context));

    // then
    assertThat(throwable)
            .isInstanceOf(ConnectorException.class)
            .hasMessageContaining("Simulated SOAP fault");
  }
}
