package org.purpleteam.track;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class HttpSender {
    String destUrl;
    int port = 8080;

    private String getHost(String hostName) {
        String result = hostName;
        int idx = result.indexOf("//");
        if (idx > -1)
            result = result.substring(idx + 2, result.length() - idx);
        idx = result.indexOf("/");
        if (idx > -1)
            result = result.substring(0, idx);
            log.info();
        return result;
    }

    private HttpData doSend(HttpData httpData) {
        HttpData result = new HttpData();
        if ((null == destUrl) && (null == httpData))
            return result;
        destUrl = (null != httpData) ? httpData.getUrl() : destUrl;
        try {
            String hostName = getHost(destUrl);
            int reqIdx = destUrl.indexOf(hostName) + hostName.length();
            String request = destUrl.substring(reqIdx);
            String[] params = hostName.split(":");
            if (params.length > 1)
                try {
                    port =  Integer.parseInt(params[1]);
                } catch (NumberFormatException ex) {
                    System.out.println(ex.getMessage());
                }
            Socket socket = new Socket(params[0], port);
            PrintWriter output = new PrintWriter(socket.getOutputStream());
            if (null == httpData)
                request = "GET " + request;
            else
                request = httpData.getHttpMethod() + " " + request + httpData.mergeRequest();
            request += " HTTP/1.1";
            output.println(request);
            if (null != httpData && httpData.getHeaders().size() > 0)
                for (String s : httpData.getHeaders())
                    output.println(s);
            else
                output.println("Content-Type: application/soap+xml; charset=utf-8");
            output.println("");
            if (null != httpData)
                output.println(httpData.getBody());
            output.flush();
            InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader input = new BufferedReader(isr);
            result.readData(input);
            socket.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    /***
     * Method to connect to any Server waiting for client connection (for ex. some destUrl) and send to it
     * request. After connection method get data from server and return it as new HttpData
     * @param request request to send (in url)
     * @return httpData response from destination
     * @throws IOException
     */
    public HttpData sendRequest(String request) {
        this.destUrl = request;
        return doSend(null);
    }

    /***
     * Method to connect to any Server waiting for client connection (for ex. some destUrl) and send to it
     * HttpData. After connection method get data from server and return it as new HttpData
     * @param httpData request as HttpData to send
     * @return httpData response from destination
     * @throws IOException
     */
    public HttpData sendRequest(HttpData httpData) {
        return doSend(httpData);
    }

    /***
     * Method to GET/POST HttpData merging all data in URL. Use it if server can not receive request in headers
     * @param httpData
     * @return httpData received from other side (url)
     */
    public HttpData sendRequestInUrl(HttpData httpData) {
        HttpData result = new HttpData();
        if ((null == destUrl) && (null == httpData))
            return result;
        try {
            destUrl = (null != httpData) ? httpData.getUrl() : destUrl;
            if (null != httpData)
                destUrl += httpData.mergeRequest();
            System.out.println(destUrl);
            URL url = new URL(destUrl);
            InputStreamReader isr = new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8);
            BufferedReader input = new BufferedReader(isr);
            result.readData(input);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}