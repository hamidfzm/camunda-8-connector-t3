# TT Connector

Camunda TT Outbound Connector

## Build

You can package the Connector by running the following command:

```bash
mvn clean package
```

This will create the following artifacts:

- A thin JAR without dependencies.
- An fat JAR containing all dependencies, potentially shaded to avoid classpath conflicts. This will not include the SDK artifacts since those are in scope `provided` and will be brought along by the respective Connector Runtime executing the Connector.

### Shading dependencies

You can use the `maven-shade-plugin` defined in the [Maven configuration](./pom.xml) to relocate common dependencies
that are used in other Connectors and the [Connector Runtime](https://github.com/camunda-community-hub/spring-zeebe/tree/master/connector-runtime#building-connector-runtime-bundles).
This helps to avoid classpath conflicts when the Connector is executed. 

Use the `relocations` configuration in the Maven Shade plugin to define the dependencies that should be shaded.
The [Maven Shade documentation](https://maven.apache.org/plugins/maven-shade-plugin/examples/class-relocation.html) 
provides more details on relocations.

## API

### Input

| Name     | Description      | Example           | Notes                                                                      |
|----------|------------------|-------------------|----------------------------------------------------------------------------|
| username | Mock username    | `alice`           | Has no effect on the function call outcome.                                |
| token    | Mock token value | `my-secret-token` | Has no effect on the function call outcome.                                |
| message  | Mock message     | `Hello World`     | Echoed back in the output. If starts with 'fail', an error will be thrown. |

### Output

```json
{
  "result": {
    "myProperty": "Message received: ..."
  }
}
```

### Error codes

| Code | Description                                |
|------|--------------------------------------------|
| FAIL | Message starts with 'fail' (ignoring case) |

## Test locally

Run unit tests

```bash
mvn clean verify
```

### Test with local runtime

Use the [Camunda Connector Runtime](https://github.com/camunda-community-hub/spring-zeebe/tree/master/connector-runtime#building-connector-runtime-bundles) to run your function as a local Java application.

In your IDE you can also simply navigate to the `LocalContainerRuntime` class in test scope and run it via your IDE.
If necessary, you can adjust `application.properties` in test scope.

## Element Template

The element template for this sample connector is generated automatically based on the connector
input class using the [Element Template Generator](https://github.com/camunda/connectors/tree/main/element-template-generator/core).
The generation is embedded in the Maven build and can be triggered by running `mvn clean package`.

It is not mandatory to generate the element template for your connector and you can also create it manually.
However, the generator provides a convenient way to create the template and keep it in sync with the connector input class
and empowers you to prototype and iterate quickly.

The generated element template can be found in [element-templates/soap-connector.json](./element-templates/soap-connector.json).
