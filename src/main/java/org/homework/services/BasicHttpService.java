package org.homework.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.homework.api.HttpService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Register
public class BasicHttpService implements HttpService {

    @Resolve
    private Logger logger;

    @Override
    public String sendGetRequest(String url, Map<String, String> headers) throws IOException, URISyntaxException {
        logger.debug("Отправка GET-запроса по URL: " + url);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            headers.forEach(request::addHeader);
            HttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }
    }

    @Override
    public String sendPostRequest(String url, Map<String, String> headers, String body) throws IOException, URISyntaxException {
        logger.debug("Отправка POST-запроса по URL: " + url);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            headers.forEach(request::addHeader);
            request.setEntity(new StringEntity(body));
            HttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }
    }

    @Override
    public String sendPutRequest(String url, Map<String, String> headers, String body) throws IOException, URISyntaxException {
        logger.debug("Отправка PUT-запроса по URL: " + url);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(url);
            headers.forEach(request::addHeader);
            request.setEntity(new StringEntity(body));
            HttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }
    }

    @Override
    public String sendDeleteRequest(String url, Map<String, String> headers) throws IOException, URISyntaxException {
        logger.debug("Отправка DELETE-запроса по URL: " + url);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(url);
            headers.forEach(request::addHeader);
            HttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }
    }
}