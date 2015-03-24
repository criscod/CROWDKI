package org.crowdsourcedinterlinking.mpublication;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class CwdfHttpClient {

	// as Sindice

	static private CwdfHttpClient singleton = null;

	private HttpClient client;

	private CwdfHttpClient() {
		client = new DefaultHttpClient();
	}

	public static synchronized CwdfHttpClient getInstance() {
		if (singleton == null) {
			singleton = new CwdfHttpClient();
		}
		return singleton;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

}
