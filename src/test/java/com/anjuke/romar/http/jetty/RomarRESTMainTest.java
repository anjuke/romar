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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.anjuke.romar.http.rest.bean.PreferenceBean;
import com.anjuke.romar.http.rest.bean.RecommendBean;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class RomarRESTMainTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("romar.config", "src/test/resources/testRomar.yaml");
        RomarRESTMain.main(args);
    }

    static Thread jettyThread;
    ObjectMapper mapper = new ObjectMapper();
    Client client;

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("romar.config", "src/test/resources/testRomar.yaml");
        jettyThread = new Thread() {
            @Override
            public void run() {
                try {
                    RomarRESTMain.main(new String[] {"8080"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        jettyThread.start();
        Thread.sleep(2000);
    }

    public static void afterClass() throws InterruptedException {
        jettyThread.interrupt();
        jettyThread.join();
    }

    @Before
    public void setUp() throws Exception {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
                Boolean.TRUE);
        client = Client.create(clientConfig);
    }

    @After
    public void tearDown() throws Exception {
        client.destroy();
    }

    @Test
    public void testDeletePreference() {
        WebResource webResource = client
                .resource("http://localhost:8080/preferences/1/2");
        ClientResponse response = null;
        response = webResource.accept("application/json").delete(ClientResponse.class);
        assertEquals(202, response.getStatus());
    }

    @Test
    public void testSetPreference() throws JsonGenerationException, JsonMappingException,
            IOException {

        WebResource webResource = client.resource("http://localhost:8080/preferences");

        PreferenceBean bean = new PreferenceBean();
        bean.setUser(1);
        bean.setItem(1);
        bean.setValue(1.0f);
        ClientResponse response = null;
        response = webResource.accept("application/json")
                .entity(mapper.writeValueAsString(bean), MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class);
        assertEquals(202, response.getStatus());
        bean = new PreferenceBean();
        bean.setUser(1);
        bean.setItem(2);
        bean.setValue(1.0f);
        response = webResource.accept("application/json")
                .entity(bean, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        assertEquals(202, response.getStatus());

        bean = new PreferenceBean();
        bean.setUser(2);
        bean.setItem(1);
        bean.setValue(1.0f);
        response = webResource.accept("application/json")
                .entity(bean, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);

        assertEquals(202, response.getStatus());

        bean = new PreferenceBean();
        bean.setUser(2);
        bean.setItem(2);
        bean.setValue(1);
        response = webResource.accept("application/json")
                .entity(bean, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);

        assertEquals(202, response.getStatus());
        bean = new PreferenceBean();
        bean.setUser(2);
        bean.setItem(3);
        bean.setValue(1.0f);
        response = null;
        response = webResource.accept("application/json")
                .entity(bean, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
        assertEquals(202, response.getStatus());
        bean = new PreferenceBean();
        bean.setUser(2);
        bean.setItem(4);
        bean.setValue(1.0f);
        response = null;
        response = webResource.accept("application/json")
                .entity(bean, MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);

        assertEquals(202, response.getStatus());
    }

    @Test
    public void testCommit() throws InterruptedException {
        WebResource webResource = client.resource("http://localhost:8080/commit");
        ClientResponse response = null;
        response = webResource.accept("application/json").post(ClientResponse.class);
        assertEquals(202, response.getStatus());
    }

    @Test
    public void testEstimatePreference() {
        WebResource webResource = client
                .resource("http://localhost:8080/preferences/1/2");
        ClientResponse response = null;
        response = webResource.accept("application/json").get(ClientResponse.class);
        Map<String, Float> map = response.getEntity(new HashMap<String, Float>()
                .getClass());
        assertEquals(1.0f, ((Number) map.get("value")).floatValue(), 0f);
    }

    @Test
    public void testRecommend() {
        WebResource webResource = client
                .resource("http://localhost:8080/users/1/recommendations");
        ClientResponse response = null;
        response = webResource.accept("application/json").get(ClientResponse.class);
        List<RecommendBean> list = response.getEntity(new ArrayList<RecommendBean>()
                .getClass());
        assertTrue(list.size() > 0);
    }

    @Test
    public void testItemSimilar() {
        WebResource webResource = client.resource("http://localhost:8080/items/similars?item=1&item=2");
        ClientResponse response = null;
        response = webResource.accept("application/json").get(ClientResponse.class);
        List<RecommendBean> list = response.getEntity(new ArrayList<RecommendBean>()
                .getClass());
        assertTrue(list.size() > 0);
    }

    @Test
    public void testUserSimilar() {
        WebResource webResource = client.resource("http://localhost:8080/users/1/similars");
        ClientResponse response = null;
        response = webResource.accept("application/json").get(ClientResponse.class);
        String result = response.getEntity(String.class);
        System.out.println(result);
    }


    @Test
    public void testUserRemove(){
        WebResource webResource = client.resource("http://localhost:8080/users/1");
        ClientResponse response = null;
        response = webResource.accept("application/json").delete(ClientResponse.class);
        assertEquals(202, response.getStatus());
    }

    @Test
    public void testItemRemove(){
        WebResource webResource = client.resource("http://localhost:8080/items/1");
        ClientResponse response = null;
        response = webResource.accept("application/json").delete(ClientResponse.class);
        assertEquals(202, response.getStatus());
    }
}
