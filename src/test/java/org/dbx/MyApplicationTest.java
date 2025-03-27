package org.dbx;

import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.main.junit5.CamelMainTestSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.apache.camel.builder.Builder.constant;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A simple unit test showing how to test the application {@link MyApplication}.
 */
class MyApplicationTest extends CamelMainTestSupport {

    @Override
    protected Class<?> getMainClass() {
        // The main class of the application to test
        return MyApplication.class;
    }



    @Disabled
    @Test
    void should_complete_the_auto_detected_route() throws Exception {

       adviceWith(context, "api", arb -> arb
               .weaveById("external-api")
               .replace()
               .to("mock:external-api"));

        // Set up the mock endpoint expectations
        MockEndpoint mockEndpoint = getMockEndpoint("mock:external-api");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.returnReplyBody(constant("{\"mockedKey\":\"mockedValue\"}")); // Mock response

        NotifyBuilder notify = new NotifyBuilder(context)
                .whenCompleted(1).whenBodiesDone("Goodbye World").create();
        assertTrue(
                notify.matches(20, TimeUnit.SECONDS), "1 message should be completed"
        );
    }
}
