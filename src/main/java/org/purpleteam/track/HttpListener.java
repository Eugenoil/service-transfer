package org.purpleteam.track;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpListener {
    private ServerSocket serverSocket = null;
    private Socket socket;
    private long propertiesUpdated;
    private File appConfigPath;
    private int port;

    /***
     * Default constructor load <port> number from file app.properties. If port not found set it to 8080
     */
    public HttpListener() throws IOException {
        readPortFromProperties();
        updateSettings();
    }

    private void readPortFromProperties() {
        try {
            String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
            appConfigPath = new File(rootPath + "app.properties");
            if  (appConfigPath.lastModified() == propertiesUpdated)
                return ;
            Properties appProps = new Properties();
            appProps.load(new FileInputStream(appConfigPath));
            try {
                this.port = Integer.parseInt(appProps.getProperty("port"));
            } catch (NumberFormatException ex){
                if (port == 0)
                    this.port = 8080;
            }
            propertiesUpdated = appConfigPath.lastModified();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /***
     * Initialize (first call) or update (when settings file changes) settings of object and create ServerSocket
     * @throws IOException
     */
    private void updateSettings() throws IOException {
        if (appConfigPath != null && appConfigPath.exists()) {
            readPortFromProperties();
            if (serverSocket != null)
                serverSocket.close();
            serverSocket = new ServerSocket(port == 0 ? 8080 : port);
        }
    }

    /***
     * Starting to listen socket and wait for client connection,
     * When client connected - receive Object httpData and return it as result.
     * Attention - method freezes while no clients connected that's why better to call it in thread
     * @return httpData object with received data
     * @throws IOException
     */
    public HttpData listen() throws IOException {
        if (appConfigPath != null)
            updateSettings();
        socket = serverSocket.accept();
        InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader input = new BufferedReader(isr);
        HttpData httpData = new HttpData();
        httpData.readData(input);
        return httpData;
    }

    /***
     * Sends response to client connected early in "listen" method. You need to call it after manipulation
     * with data got in "listen" method because client be waiting for some response or be looping waiting.
     * @param httpData is data to send in response.
     * @throws IOException
     */
    public void sendResponse(HttpData httpData) throws IOException {
        PrintWriter output = new PrintWriter(socket.getOutputStream());
        if (httpData.getHeaders().size() > 0) {
            for (String s : httpData.getHeaders()) {
                output.println(s);
            }
        } else {
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
        }
        output.println();
        output.println(httpData.getBody());
        output.flush();
        socket.close();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}