/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.johnzon.mapper;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CircularExceptionTest {
    @Test
    public void dontStackOverFlow() {
        final Throwable oopsImVicous = new Exception("circular");
        oopsImVicous.getStackTrace(); // fill it
        oopsImVicous.initCause(new IllegalArgumentException(oopsImVicous));
        final String serialized = new MapperBuilder().setAccessModeName("field").build().writeObjectAsString(oopsImVicous);
        assertTrue(serialized.contains("\"detailMessage\":\"circular\""));
        assertTrue(serialized.contains("\"stackTrace\":[{"));
    }

    @Test
    public void testCyclicPerson() {
        Person john = new Person("John");
        Person marry = new Person("Marry");

        john.setMarriedTo(marry);
        marry.setMarriedTo(john);

        String ser = new MapperBuilder().setAccessModeName("field").build().writeObjectAsString(john);
        Assert.assertNotNull(ser);
        assertTrue(ser.contains("\"name\":\"John\""));
        assertTrue(ser.contains("\"marriedTo\":\"/\""));
        assertTrue(ser.contains("\"name\":\"Marry\""));
    }

    public static class Person {
        private final String name;
        private Person marriedTo;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Person getMarriedTo() {
            return marriedTo;
        }

        public void setMarriedTo(Person marriedTo) {
            this.marriedTo = marriedTo;
        }
    }
}
