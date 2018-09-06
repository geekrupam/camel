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
package org.apache.camel.processor;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

/**
 * @version 
 */
public class ReduceStacksNeededDuringRoutingTest extends ContextTestSupport {

    @Override
    protected boolean useJmx() {
        return true;
    }

    @Test
    public void testReduceStacksNeeded() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");

        template.sendBody("seda:start", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // context.setTracing(true);

                from("seda:start")
                        .to("log:foo")
                        .to("log:bar")
                        .to("log:baz")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                try {
                                    throw new IllegalArgumentException("Forced to dump stacktrace");
                                } catch (Exception e) {
                                    e.fillInStackTrace();
                                    log.info("There are " + e.getStackTrace().length + " lines in the stacktrace");
                                    log.error("Dump stacktrace to log", e);
                                }
                            }
                        })
                        .to("mock:result");
            }
        };
    }
}
