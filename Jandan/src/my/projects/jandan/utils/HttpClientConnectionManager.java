package my.projects.jandan.utils;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

public class HttpClientConnectionManager {

	private static ThreadSafeClientConnManager cm;

	public final static int MAX_TOTAL_CONNECTIONS = 800;
	public final static int MAX_ROUTE_CONNECTIONS = 400;
	public final static int CONNECT_TIMEOUT = 10000;
	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
				.getSocketFactory()));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				schemeRegistry);
		// Increase max total connection to 200
		cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
	}

	public static DefaultHttpClient getHttpClient() {
		return new DefaultHttpClient(cm);
	}

}
