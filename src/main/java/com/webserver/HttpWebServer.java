package com.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HttpWebServer {
    public static void main( String[] args ) throws IOException {
        System.out.println("Hello here from smit!");
        try (ServerSocket serverSocket = new ServerSocket(8080)){
            //need the infinity loop to 'pause the server'.
            while(true){
                //To accept connection from client we have to call blocking accept() method. Java program will wait for a client on that line.
                try (Socket client = serverSocket.accept()){
                    handleClient(client);
                }
            }
        }
    }

    private static void handleClient(Socket client) throws IOException {
        System.out.println("Client : " + client);
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        StringBuilder requestBuilder = new StringBuilder();
        String line;
        //In HTTP every new line separator is a Window's new line. \r\n
        while(!(line = br.readLine()).isBlank()){
            requestBuilder.append(line + "\r\n");
        }
        String request = requestBuilder.toString();
        System.out.println(request);

        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];

        List<String> headers = new ArrayList<>();
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }

        String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                client.toString(), method, path, version, host, headers.toString());
        System.out.println(accessLog);

        /*
        HTTP Request format :
            version status code
            headers
            (empty line)
            content
            (empty line)
        */
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
        clientOutput.write(("ContentType: text/html\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write("<b>It works!</b>".getBytes());
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        client.close();
    }
}
