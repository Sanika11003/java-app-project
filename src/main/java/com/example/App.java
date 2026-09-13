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
<<<<<<< HEAD
			"<h1>This is project to use webhook in jenkins to test automation</h1>" +
=======
			"<h1>This is project to check webhook integration in jenkins</h1>" +
>>>>>>> e234548 (Update)
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


