package com.dienen.httpClient.utils;

import com.google.gson.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by preetraj on 7/7/17.
 * This is custom Http Rest Utility that offers the capability to make restful calls. This utility is developed using
 * Apache's CloseableHttpClient that provides a better handling of rest calls.
 * This utility is complied using java 1.8, please make sure to use 1.7 or greater.
 */
@Component
public class HttpClientTemplate {

    private Logger logger = LoggerFactory.getLogger(HttpClientTemplate.class);
    private final int SUCCESS_RESPONSE_CODE = 200;
    private final int NOT_FOUND_RESPONSE_CODE = 404;
    private final int NOT_AUTHORIZED_RESPONSE_CODE = 401;

    private Gson gson() {
        return new GsonBuilder().create();
    }

    /**
     * @param url  - non-encoded url string of the rest resource
     * @param type - this is the expected response type, sample usage new TypeToken<List<Map<String,Object>>>(){}.getType()
     *             here, List can be replaced by anyother collection type and Map<String,Object> with a custom POJO
     * @return - returns an expected Collection of JAVA objects specified in the type param
     */
    public <T> List<T> getForList(String url, Type type, Map<String, String> customHeaders) {
        List<T> response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (null != customHeaders) {
                customHeaders.forEach(httpGet::addHeader);
            }
            response = getResponse(httpResponse, type);
        } catch (Exception e) {
            logger.error("Exception occurred while making a get request to {}", url, e);
        }
        return response;
    }

    /**
     * @param url  - non-encoded url string of the rest resource
     * @param type - this is the expected response type, sample usage new TypeToken<Object>(){}.getType()
     *             here, <Object> can be replaced with anyother custom POJO
     * @return - returns an expected JAVA object specified in the type param
     */
    public <T> T getForObject(String url, Type type, Map<String, String> customHeaders) {
        T response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (null != customHeaders) {
                customHeaders.forEach(httpGet::addHeader);
            }
            response = getResponse(httpResponse, type);
        } catch (Exception e) {
            logger.error("Exception occurred while making a get request to {}", url);
        }
        return response;
    }

    /**
     * @param url     - non-encoded url string of the rest resource
     * @param type    - this is the expected response type, sample usage new TypeToken<Object>(){}.getType()
     *                here, <Object> can be replaced with anyother custom POJO
     * @param configs - custom Post params that can be sent as part of the Post call, can be null if not required
     * @return - returns an expected JAVA object specified in the type param
     */
    public <T> T postForObject(String url, Type type, Map<String, String> configs, Map<String, String> customHeaders) {
        T response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> requestObject = new ArrayList<>();

            if (null != configs) {
                configs.forEach((x, y) -> {
                    requestObject.add(new BasicNameValuePair(x, y));
                });
            }
            if (null != customHeaders) {
                customHeaders.forEach(httpPost::addHeader);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(requestObject));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            response = getResponse(httpResponse, type);
        } catch (Exception e) {
            logger.error("Exception occurred while making a get request to {}", url, e);
        }
        return response;
    }

    /**
     * @param url     - non-encoded url string of the rest resource
     * @param type    - this is the expected response type, sample usage new TypeToken<Object>(){}.getType()
     *                here, <Object> can be replaced with anyother custom POJO
     * @param configs - custom Post params that can be sent as part of the Post call, can be null if not required
     * @return - returns an expected Collection of JAVA objects specified in the type param
     */
    public <T> T postForList(String url, Type type, Map<String, String> configs, Map<String, String> customHeaders) {
        T response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> requestObject = new ArrayList<>();

            if (null != configs) {
                configs.forEach((x, y) -> {
                    requestObject.add(new BasicNameValuePair(x, y));
                });
            }
            if (null != customHeaders) {
                customHeaders.forEach(httpPost::addHeader);
            }

            httpPost.setEntity(new UrlEncodedFormEntity(requestObject));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            response = getResponse(httpResponse, type);

        } catch (Exception e) {
            logger.error("Exception occurred while making a get request to {}", url, e);
        }
        return response;
    }

    /**
     * @return - returns a generic response type object based on httpResponse
     * @throws IOException
     */
    private <T> T getResponse(HttpResponse httpResponse, Type type) throws IOException {

        T response = null;
        switch (httpResponse.getStatusLine().getStatusCode()) {
            case SUCCESS_RESPONSE_CODE:
                String responseEntity = EntityUtils.toString(httpResponse.getEntity());
                response = gson().fromJson(responseEntity, type);
                break;
            case NOT_FOUND_RESPONSE_CODE:
                throw new IllegalStateException("The requested url could not be found: " + httpResponse.getStatusLine());
            case NOT_AUTHORIZED_RESPONSE_CODE:
                throw new IllegalStateException("Access to this resource is forbidden " + httpResponse.getStatusLine());
        }

        return response;
    }

    /**
     * @param url     - non-encoded url string of the rest resource
     * @param type    - this is the expected response type, sample usage new TypeToken<Object>(){}.getType()
     *                here, <Object> can be replaced with anyother custom POJO
     * @param configs - custom Post params that can be sent as part of the Post call, can be null if not required
     * @return - returns an expected JAVA object specified in the type param, in most cases this could be blank.
     */
    public <T> T put(String url, Type type, Map<String, String> configs, Map<String, String> customHeaders) {
        T response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPut httpPut = new HttpPut(url);
            List<NameValuePair> requestObject = new ArrayList<>();

            if (null != configs) {
                configs.forEach((x, y) -> {
                    requestObject.add(new BasicNameValuePair(x, y));
                });
            }
            if (null != customHeaders) {
                customHeaders.forEach(httpPut::addHeader);
            }
            httpPut.setEntity(new UrlEncodedFormEntity(requestObject));
            HttpResponse httpResponse = httpClient.execute(httpPut);
            response = getResponse(httpResponse, type);
        } catch (Exception e) {
            logger.error("Exception occurred while making a get request to {}", url, e);
        }
        return response;
    }

    /**
     * @param url  - non-encoded url string of the rest resource
     * @param type - this is the expected response type, sample usage new TypeToken<Object>(){}.getType()
     *             here, <Object> can be replaced with anyother custom POJO
     * @return - returns an expected JAVA object specified in the type param, in most cases this could be blank.
     */
    public <T> T delete(String url, Type type, Map<String, String> customHeaders) {
        T response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpDelete httpPost = new HttpDelete(url);
            if (null != customHeaders) {
                customHeaders.forEach(httpPost::addHeader);
            }
            HttpResponse httpResponse = httpClient.execute(httpPost);
            response = getResponse(httpResponse, type);
        } catch (Exception e) {
            logger.error("Exception occurred while making a get request to {}", url, e);
        }
        return response;
    }
}
