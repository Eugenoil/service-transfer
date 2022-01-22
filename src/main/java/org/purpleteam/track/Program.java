package org.purpleteam.track;

import org.purpleteam.*;
import org.jdom2.Element;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPMessage;
import java.util.Iterator;
import java.util.List;

public class Program {
    /***
     * Example of creating listening server. Var <request> receive HttpData which contains
     * Map httpData.parameters() and httpData.method() to understand what data need to put in sendResponse()
     */
    public static void listen() throws Exception {
        HttpListener rs = new HttpListener();
        System.out.println("Service listener started");
        HttpData request;
        while (true) {
            request = rs.listen();
            // some operation with request
            // check request.parameters() and request.getMethod();
            System.out.println(request);
            HttpData httpData = new HttpData();
            httpData.setBody("<p>Hello!</p>");
            rs.sendResponse(httpData);
        }
    }

    /***
     * Example of sending request in form of HttpData. Parameters included in headers, not inside URL
     */
    public static void sendHttpData() {
        HttpData httpData = new HttpData();
        httpData.setUrl("localhost:8080");
        httpData.addParamenets("trackings", "new");
        httpData.addParamenets("id", "1");
        httpData.addParamenets("task_name", "New task started");
        httpData.setHttpMethod("POST");
        System.out.println(new HttpSender().sendRequestInUrl(httpData));
    }

    /***
     * Example of sending HttpData in URL (not headers)
     */
    public static void sendHttpDataInUrl() {
        HttpData httpData = new HttpData();
        httpData.setUrl("localhost:8080");
        httpData.addParamenets("trackings", "list");
        httpData.addParamenets("id", "1");
        httpData.setHttpMethod("GET");
        System.out.println(new HttpSender().sendRequestInUrl(httpData));
    }

    public static void main(String[] args) throws Exception {
//        sendHttpData();
        listen();
    }
}
