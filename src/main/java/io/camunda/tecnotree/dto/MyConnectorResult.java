package io.camunda.tecnotree.dto;

public class MyConnectorResult {
    private String response;

    public MyConnectorResult(String response) {
        this.response = response;
    }

    // Getter and Setter

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
