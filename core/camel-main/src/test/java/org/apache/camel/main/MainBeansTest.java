/*
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
package org.apache.camel.main;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class MainBeansTest {

    @Test
    public void testBindBeans() throws Exception {
        MyFoo myFoo = new MyFoo();

        Main main = new Main();
        main.configure().addRoutesBuilder(new MyRouteBuilder());
        main.bind("myFoolish", myFoo);

        // create by class
        main.addProperty("camel.beans.foo", "#class:org.apache.camel.main.MySedaBlockingQueueFactory");
        main.addProperty("camel.beans.foo.counter", "123");

        // lookup by type
        main.addProperty("camel.beans.myfoo", "#type:org.apache.camel.main.MyFoo");
        main.addProperty("camel.beans.myfoo.name", "Donkey");
        main.start();

        CamelContext camelContext = main.getCamelContext();
        assertNotNull(camelContext);

        Object foo = camelContext.getRegistry().lookupByName("foo");
        assertNotNull(foo);

        MySedaBlockingQueueFactory myBQF
                = camelContext.getRegistry().findByType(MySedaBlockingQueueFactory.class).iterator().next();
        assertSame(foo, myBQF);

        assertEquals(123, myBQF.getCounter());
        assertEquals("Donkey", myFoo.getName());

        main.stop();
    }

    @Test
    public void testBindBeansMap() throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new MyRouteBuilder());

        // create by class
        main.addProperty("camel.beans.foo", "#class:java.util.HashMap");
        main.addProperty("camel.beans.foo.database", "mydb");
        main.addProperty("camel.beans.foo.username", "scott");
        main.addProperty("camel.beans.foo.password", "tiger");

        main.start();

        CamelContext camelContext = main.getCamelContext();
        assertNotNull(camelContext);

        Map foo = camelContext.getRegistry().lookupByNameAndType("foo", Map.class);
        assertNotNull(foo);

        assertEquals(3, foo.size());
        assertEquals("mydb", foo.get("database"));
        assertEquals("scott", foo.get("username"));
        assertEquals("tiger", foo.get("password"));

        main.stop();
    }

    @Test
    public void testBindBeansMapSquareClass() throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new MyRouteBuilder());

        // create by class
        main.addProperty("camel.beans.foo", "#class:java.util.HashMap");
        main.addProperty("camel.beans.foo[database]", "mydb");
        main.addProperty("camel.beans.foo[username]", "scott");
        main.addProperty("camel.beans.foo[password]", "tiger");

        main.start();

        CamelContext camelContext = main.getCamelContext();
        assertNotNull(camelContext);

        Map foo = camelContext.getRegistry().lookupByNameAndType("foo", Map.class);
        assertNotNull(foo);

        assertEquals(3, foo.size());
        assertEquals("mydb", foo.get("database"));
        assertEquals("scott", foo.get("username"));
        assertEquals("tiger", foo.get("password"));

        main.stop();
    }

    @Test
    public void testBindBeansMapSquare() throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new MyRouteBuilder());

        // create by class
        main.addProperty("camel.beans.foo[database]", "mydb");
        main.addProperty("camel.beans.foo[username]", "scott");
        main.addProperty("camel.beans.foo[password]", "tiger");

        main.start();

        CamelContext camelContext = main.getCamelContext();
        assertNotNull(camelContext);

        Map foo = camelContext.getRegistry().lookupByNameAndType("foo", Map.class);
        assertNotNull(foo);

        assertEquals(3, foo.size());
        assertEquals("mydb", foo.get("database"));
        assertEquals("scott", foo.get("username"));
        assertEquals("tiger", foo.get("password"));

        main.stop();
    }

    public static class MyRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("direct:start").to("mock:foo");
        }
    }

}
