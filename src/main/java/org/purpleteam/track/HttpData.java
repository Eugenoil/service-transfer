package org.purpleteam.track;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpData {
    private String httpMethod;
    private Map<String, String> parameters;
    private List<String> headers;
    private String body;
    private String url;

    public HttpData() {
        this.parameters = new HashMap<String, String>();
        this.headers = new LinkedList<String>();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void addHeader(String headerLine) {
        this.headers.add(headerLine);
    }
    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void addParamenets(String key, String value) {
        key = URLEncoder.encode(key, StandardCharsets.UTF_8);
        value = URLEncoder.encode(value, StandardCharsets.UTF_8);
        this.parameters.put(key, value);
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /***
     * Read data from input BufferedReader and split it to request method, headers and body.
     * All of this put into curren object
     * @param input
     */
    public void readData(BufferedReader input) {
        try {
            String line = input.readLine();
            if (!line.equals("")) {
                String[] method = line.split(" ");
                setHttpMethod(method[0]);
                if (method.length > 1)
                    setParameters(splitRequest(method[1]));
                addHeader(line);
                while (input.ready() && line != "") {
                    line = input.readLine();
                    addHeader(line);
                }
                StringBuilder sb = new StringBuilder();
                while (input.ready()) {
                    line = input.readLine();
                    sb.append(line);
                }
                setBody(sb.toString());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Map<String, String> splitRequest(String request) {
        Map<String, String> result = new HashMap<>();
        String[] paramsArray = request.split("&");
        for (String s : paramsArray) {
            if (s.equals(""))
                continue;
            String clearedParam = s.replace("/?", "");
            clearedParam = clearedParam.replace("\\?", "");
            clearedParam = URLDecoder.decode(clearedParam, StandardCharsets.UTF_8);
            String[] keyValue = clearedParam.split("=");
            if (keyValue.length == 1)
                result.put(keyValue[0], "");
            else
                result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    public String mergeRequest() {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = getParameters().keySet();
        int idx = 0;
        for (String key : keys) {
            if (key.equals(""))
                continue;
            String param = key + "=" + getParameters().get(key);
            if (idx < keys.size() - 1)
                param += "&";
            sb.append(param);
            idx++;
        }
        if (sb.length() == 0)
            return ("");
        return ("?" + sb.toString());
    }

    @Override
    public String toString() {
        return "HttpData{" +
                "httpMethod='" + httpMethod + '\'' +
                ", parameters=" + parameters +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
