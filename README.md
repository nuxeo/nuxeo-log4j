## Nuxeo Log4j

Nuxeo Log4j2 bundle lets users to contribute log4j2 logger configurations:

 - Adding categories
 - Setting Levels

## Import library - Compatible only Nuxeo 11.x

If you want to include this library add this to your Nuxeo package pom:

```
<groupId>org.nuxeo.log4j</groupId>
<artifactId>nuxeo-log4j</artifactId>
<version>1.0.0</version>
```

## Add a custom contribution

You can add in any Nuxeo xml component a following example of log4j2 contribution:

```
<extension target="org.nuxeo.activate.log4j.service.Log4jComponent" point="log4j">
    <log4j name="ai-core">
        <loggers>
            <Logger name="org.nuxeo.ai.cloud" level="DEBUG"/>
            <Logger name="org.nuxeo.ai" level="INFO"/>
        </loggers>
    </log4j>
</extension>
```