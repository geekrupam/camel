/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor.onexception;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

/**
 * @version 
 */
public class DoCatchDirectDynamicRouteTest extends ContextTestSupport {

    private int counter;

    @Test
    public void testDoCatchDirectRoute() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        getMockEndpoint("mock:c").expectedMessageCount(1);

        template.sendBody("direct:start", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .doTry()
                        .to("direct:a")
                    .doCatch(Exception.class)
                        .to("direct:c")
                    .end();

                from("direct:a")
                    .to("mock:a")
                    .dynamicRouter(method(DoCatchDirectDynamicRouteTest.class, "next"));

                from("direct:b")
                    .to("mock:b")
                    .throwException(new IllegalArgumentException("Forced"));

                from("direct:c")
                    .to("mock:c");
            }
        };
    }

    public String next() {
        if (counter++ == 0) {
            return "direct:b";
        } else {
            return null;
        }
    }
}
