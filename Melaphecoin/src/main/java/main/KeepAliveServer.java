package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class KeepAliveServer extends Thread {
    public static int port = 9000;

    @Override
    public void run() {
	HttpServer server = null;
	try {
	    server = HttpServer.create(new InetSocketAddress(port), 0);
	} catch (Exception ignored) {
	}
	System.out.println("server started at " + port);
	server.createContext("/", new RootHandler());
	server.createContext("/echoHeader", new EchoHeaderHandler());
	server.createContext("/echoGet", new EchoGetHandler());
	server.createContext("/echoPost", new EchoPostHandler());
	server.setExecutor(null);
	server.start();
    }

    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

	if (query != null) {
	    String pairs[] = query.split("[&]");
	    for (String pair : pairs) {
		String param[] = pair.split("[=]");
		String key = null;
		String value = null;
		if (param.length > 0) {
		    key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
		}

		if (param.length > 1) {
		    value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
		}

		if (parameters.containsKey(key)) {
		    Object obj = parameters.get(key);
		    if (obj instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<String> values = (List<String>) obj;
			values.add(value);

		    } else if (obj instanceof String) {
			List<String> values = new ArrayList<String>();
			values.add((String) obj);
			values.add(value);
			parameters.put(key, values);
		    }
		} else {
		    parameters.put(key, value);
		}
	    }
	}
    }
}

class RootHandler implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
	String response = "<h1>Server start success if you see this message</h1>" + "<h1>Port: " + KeepAliveServer.port
		+ "</h1>";
	he.sendResponseHeaders(200, response.length());
	OutputStream os = he.getResponseBody();
	os.write(response.getBytes());
	os.close();
    }
}

class EchoHeaderHandler implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
	Headers headers = he.getRequestHeaders();
	Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
	String response = "";
	for (Map.Entry<String, List<String>> entry : entries)
	    response += entry.toString() + "\n";
	he.sendResponseHeaders(200, response.length());
	OutputStream os = he.getResponseBody();
	os.write(response.toString().getBytes());
	os.close();
    }
}

class EchoGetHandler implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
	// parse request
	Map<String, Object> parameters = new HashMap<String, Object>();
	URI requestedUri = he.getRequestURI();
	String query = requestedUri.getRawQuery();
	KeepAliveServer.parseQuery(query, parameters);

	// send response
	String response = "";
	for (String key : parameters.keySet())
	    response += key + " = " + parameters.get(key) + "\n";
	he.sendResponseHeaders(200, response.length());
	OutputStream os = he.getResponseBody();
	os.write(response.toString().getBytes());

	os.close();
    }
}

class EchoPostHandler implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
	// parse request
	Map<String, Object> parameters = new HashMap<String, Object>();
	InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
	BufferedReader br = new BufferedReader(isr);
	String query = br.readLine();
	KeepAliveServer.parseQuery(query, parameters);

	// send response
	String response = "";
	for (String key : parameters.keySet())
	    response += key + " = " + parameters.get(key) + "\n";
	he.sendResponseHeaders(200, response.length());
	OutputStream os = he.getResponseBody();
	os.write(response.toString().getBytes());
	os.close();
    }
}