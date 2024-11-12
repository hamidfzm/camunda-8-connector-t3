package io.camunda.tecnotree;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import io.camunda.tecnotree.dto.MyConnectorRequest;
import io.camunda.tecnotree.dto.MyConnectorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@OutboundConnector(
        name = "SOAPConnector",
        inputVariables = {"authentication", "soapEndpoint", "soapAction", "soapBody"},
        type = "io.camunda:soap-connector:1")
@ElementTemplate(
        id = "io.camunda.connector.SOAPConnector.v1",
        name = "SOAP Connector",
        version = 1,
        description = "Connector to call SOAP services with basic authentication",
        icon = "icon.svg",
        documentationRef = "https://docs.camunda.io/docs/components/connectors/out-of-the-box-connectors/available-connectors-overview/",
        propertyGroups = {
                @ElementTemplate.PropertyGroup(id = "authentication", label = "Authentication"),
                @ElementTemplate.PropertyGroup(id = "request", label = "SOAP Request")
        },
        inputDataClass = MyConnectorRequest.class)
public class MyConnectorFunction implements OutboundConnectorFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyConnectorFunction.class);

    @Override
    public Object execute(OutboundConnectorContext context) {
        final var connectorRequest = context.bindVariables(MyConnectorRequest.class);
        return executeConnector(connectorRequest);
    }

    protected MyConnectorResult executeConnector(final MyConnectorRequest connectorRequest) {
        LOGGER.info("Executing SOAP connector with request {}", connectorRequest);

        try {
            String username = connectorRequest.getAuthentication().user();
            String password = connectorRequest.getAuthentication().password();
            String soapEndpoint = connectorRequest.getSoapEndpoint();
            String soapAction = connectorRequest.getSoapAction();
            String soapBody = connectorRequest.getSoapBody();

            // Create URL object
            URL url = new URI(soapEndpoint).toURL();

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set properties
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction", soapAction);

            // Set basic authentication
            if (username != null && password != null) {
                String authString = username + ":" + password;
                String authStringEnc = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
                connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            }

            // Send SOAP request
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(soapBody.getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            // Get response
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            String responseString = baos.toString(StandardCharsets.UTF_8);
            inputStream.close();

            LOGGER.info("SOAP Response: {}", responseString);

            return new MyConnectorResult(responseString);
        } catch (Exception e) {
            LOGGER.error("Error executing SOAP request", e);
            throw new ConnectorException("SOAP_ERROR", "Error executing SOAP request: " + e.getMessage());
        }
    }
}
