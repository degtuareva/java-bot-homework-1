package org.homework.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface HttpService {
    String sendGetRequest(String url, Map<String, String> headers) throws IOException, URISyntaxException;

    String sendPostRequest(String url, Map<String, String> headers, String body) throws IOException, URISyntaxException;

    String sendPutRequest(String url, Map<String, String> headers, String body) throws IOException, URISyntaxException;

    String sendDeleteRequest(String url, Map<String, String> headers) throws IOException, URISyntaxException;
}