package com.example;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {

	public static void main(String[] args) throws IOException {
		
		HttpServer server =
			HttpServer.create(new InetSocketAddress(8082), 0);
	
	server.createContext("/", exchange -> {

		String response  =
			"<html>" +
			"<head><title>DevOps Project</title></head>" +
			"<body>" +
			"<h1>This is project to push image in ECR</h1>" +
			"<h2>Java Application</h2>" +
			"<p>Running inside a docker container</p>" +
			"<p>Docker -Port mapping Practical</p>" +
			"</body>" +
			"</html>";

	exchange.sendResponseHeaders(200, response.length());

	OutputStream outputStream =exchange.getResponseBody();

	outputStream.write(response.getBytes());

	outputStream.close();
});

	server.start();
}
}


