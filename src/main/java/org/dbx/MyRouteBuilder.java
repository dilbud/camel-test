package org.dbx;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class MyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:foo?period={{myPeriod}}")
                .bean("myBean", "hello")
                .log("${body}")
                .to("direct:api");

        // Ensure this route exists
        from("direct:api")
                .routeId("api")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("https://api.restful-api.dev/objects")
                .to("direct:return");

        // Existing return route
        from("direct:return")
                .routeId("return")
                .log("${body}")
                .bean("myBean", "bye")
                .log("${body}");
    }
}
