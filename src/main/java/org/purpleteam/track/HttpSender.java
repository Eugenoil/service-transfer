package org.purpleteam.track;

import com.sun.security.jgss.GSSUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
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
        return result;
    }

    private HttpData doSend(HttpData httpData) {
        HttpData result = new HttpData();
        if (destUrl == null && httpData == null)
            return result;
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
            if (httpData != null)
                request = httpData.getHttpMethod() + " /?" + httpData.mergeRequest() + " HTTP/1.1";
            else
                request = "GET " + request + " HTTP/1.1";
            output.println(request);
            if (httpData != null && httpData.getHeaders().size() > 0)
                for (String s : httpData.getHeaders())
                    output.println(s);
            output.println();
            if (httpData != null)
                output.println(httpData.getBody());
            output.flush();
            InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader input = new BufferedReader(isr);
            result.readData(input);
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

    public String getUrl() {
        return destUrl;
    }

    public void setUrl(String destUrl) {
        this.destUrl = destUrl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}