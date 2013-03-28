/**
 * Copyright 2012 Anjuke Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anjuke.romar.http.jetty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.core.RomarConfig;
import com.anjuke.romar.http.rest.bean.RecommendBean;
import com.anjuke.romar.http.rest.bean.ValueBean;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class RomarRESTMainTest {


    private static final Logger LOG=LoggerFactory.getLogger(RomarRESTMainTest.class);
    public static void main(String[] args) throws Exception {
        System.setProperty("romar.config", "src/test/resources/testRomar.yaml");
        RomarRESTMain.main(args);
    }

    static Thread jettyThread;
    static int port;
    ObjectMapper mapper = new ObjectMapper();
    Client client;

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("romar.config", "src/test/resources/testRomar.yaml");

    }

    @Before
    public void setUp() throws Exception {
        jettyThread = new Thread() {
            @Override
            public void run() {
                try {
                    RomarRESTMain.main(new String[] {});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        jettyThread.start();
        Thread.sleep(2000);
        port = RomarConfig.getInstance().getServerPort();
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
                Boolean.TRUE);
        client = Client.create(clientConfig);
    }

    @After
    public void tearDown() throws Exception {
        client.destroy();
        jettyThread.interrupt();
        jettyThread.join();
        Thread.sleep(2000);
    }



    @Test
    public void testDeletePreference() {
        WebResource webResource = client.resource("http://localhost:" + port
                + "/preferences/1/2");
        ClientResponse response = null;
        response = webResource.accept("application/json").delete(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
    }

    public void testSetPreference() throws JsonGenerationException, JsonMappingException,
            IOException {

        WebResource webResource;
        ValueBean value = new ValueBean();
        value.setValue(1.0f);

        webResource = client.resource("http://localhost:" + port + "/preferences/1/1");
        ClientResponse response = null;
        response = webResource.accept("application/json")
                .entity(value, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
        webResource = client.resource("http://localhost:" + port + "/preferences/1/2");
        response = webResource.accept("application/json")
                .entity(value, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());

        webResource = client.resource("http://localhost:" + port + "/preferences/2/1");
        response = webResource.accept("application/json")
                .entity(value, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());

        webResource = client.resource("http://localhost:" + port + "/preferences/2/2");
        response = webResource.accept("application/json")
                .entity(value, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
        webResource = client.resource("http://localhost:" + port + "/preferences/2/3");
        response = null;
        response = webResource.accept("application/json")
                .entity(value, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
        webResource = client.resource("http://localhost:" + port + "/preferences/2/4");
        response = null;
        response = webResource.accept("application/json")
                .entity(value, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
    }

    @Test
    public void testCommit() throws InterruptedException, JsonGenerationException, JsonMappingException, IOException {
        testSetPreference();
        WebResource webResource = client.resource("http://localhost:" + port + "/commit");
        ClientResponse response = null;
        response = webResource.accept("application/json").post(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
    }

    @Test
    public void testEstimatePreference() throws JsonGenerationException, JsonMappingException, IOException {
        testSetPreference();
        WebResource webResource = client.resource("http://localhost:" + port
                + "/preferences/1/2");
        ClientResponse response = null;
        response = webResource.accept("application/json").get(ClientResponse.class);
        Map<String, Float> map = response.getEntity(new HashMap<String, Float>()
                .getClass());
        assertEquals(1.0f, ((Number) map.get("value")).floatValue(), 0f);
    }

    @Test
    public void testRecommend() throws JsonGenerationException, JsonMappingException, IOException {
        testSetPreference();
        WebResource webResource = client.resource("http://localhost:" + port
                + "/users/1/recommendations");
        ClientResponse response = null;
        response = webResource.accept("application/json").get(ClientResponse.class);
        List<RecommendBean> list = response.getEntity(new ArrayList<RecommendBean>()
                .getClass());
        assertTrue(list.size() > 0);
    }

    @Test
    public void testItemSimilar() throws JsonGenerationException, JsonMappingException, IOException {
        testSetPreference();
        WebResource webResource = client.resource("http://localhost:" + port
                + "/items/similars?item=1&item=2");
        ClientResponse response = null;
        response = webResource.accept("application/json").get(ClientResponse.class);
        List<RecommendBean> list = response.getEntity(new ArrayList<RecommendBean>()
                .getClass());
        assertTrue(list.size() > 0);
    }

    @Test
    public void testUserSimilar() throws JsonGenerationException, JsonMappingException, IOException {
        testSetPreference();
        WebResource webResource = client.resource("http://localhost:" + port
                + "/users/1/similars");
        ClientResponse response = null;
        response = webResource.accept("application/json").get(ClientResponse.class);
        traceResponse(response);
    }

    @Test
    public void testUserRemove() throws JsonGenerationException, JsonMappingException, IOException {
        testSetPreference();
        WebResource webResource = client
                .resource("http://localhost:" + port + "/users/1");
        ClientResponse response = null;
        response = webResource.accept("application/json").delete(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
    }

    @Test
    public void testItemRemove() throws JsonGenerationException, JsonMappingException, IOException {
        testSetPreference();
        WebResource webResource = client
                .resource("http://localhost:" + port + "/items/1");
        ClientResponse response = null;
        response = webResource.accept("application/json").delete(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
    }

    @Test
    public void testMultiSetPrefs() {
        WebResource webResource;

        webResource = client.resource("http://localhost:" + port + "/preferences");
        ClientResponse response = null;
        response = webResource
                .accept("application/json")
                .entity(new long[][] {new long[] {1, 1, 1}, new long[] {1, 1, 1}},
                        MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        traceResponse(response);
        assertEquals(202, response.getStatus());
    }


    private void traceResponse(ClientResponse response){
        LOG.info("===========Response with code "+response.getStatus()+"================");
        LOG.info(response.getEntity(String.class));
        LOG.info("<<<<<<<<<<<<<<Response END>>>>>>>>>>>>>>");
    }
}
