package org.dbx;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.xslt.XsltComponent;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class XsltRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        var xsltComponent = getCamelContext().getComponent("xslt", XsltComponent.class);
        xsltComponent.setUriResolver(new CustomXsltUriResolver<>(XsltRouteBuilder.class));

        from("scheduler://myScheduler?delay=1000&repeatCount=1")
                .setBody(constant(Files.readString(Paths.get("input/input.xml"))))
                .to("xslt:classpath:example.xslt")
                .process(exchange -> {
                    String content = exchange.getIn().getBody(String.class);
                    Path path = Paths.get("output/output.xml");
                    Files.createDirectories(path.getParent());
                    Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                });
    }
}