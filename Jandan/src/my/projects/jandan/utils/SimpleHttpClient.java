package my.projects.jandan.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class SimpleHttpClient {

	private ThreadLocal<DefaultHttpClient> httpclient = new ThreadLocal<DefaultHttpClient>();

	public static int imageName = 0;

	public SimpleHttpClient() {
		DefaultHttpClient client = HttpClientConnectionManager.getHttpClient();
		// 模拟浏览器，解决一些服务器程序只允许浏览器访问的问题
		client.getParams()
				.setParameter(CoreProtocolPNames.USER_AGENT,
						"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
		client.getParams().setParameter(
				CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");

		client.setHttpRequestRetryHandler(requestRetryHandler);
		httpclient.set(client);
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public String get(String url) {
		HttpGet hg = new HttpGet(url);
		hg.addHeader("Referer", "http://jandan.net/ooxx");
		hg.addHeader(
				"Accept",
				"application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*");
		String resposne = "";
		try {
			synchronized (responseHandler) {
				resposne = httpclient.get().execute(hg, responseHandler);
			}
		} catch (Exception e) {
			resposne = "";
		} finally {
			hg.abort();
		}
		return resposne;
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 *            文件http地址
	 * @param dir
	 *            目标文件
	 * @throws IOException
	 */
	public synchronized void downloadFile(String url, String dir)
			throws Exception {
		if (url == null || "".equals(url)) {
			return;
		}
		if (dir == null || "".equals(dir)) {
			return;
		}
		if (!dir.endsWith(File.separator)) {
			dir += File.separator;
		}
		File desPathFile = new File(dir);
		if (!desPathFile.exists()) {
			desPathFile.mkdirs();
		}
		String fullPath = dir + (imageName++);

		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Referer", "http://jandan.net/ooxx");

		HttpResponse response = httpclient.get().execute(httpget);
		String type = null;
		try {

			type = response.getHeaders("Content-Type")[0].getValue();

			if (type == null || "".equals(type)) {
				type = "jpg";
			} else {
				type = type.substring(type.lastIndexOf("/") + 1, type.length());
			}
		} catch (Exception e) {
			type = "jpg";
		}

		fullPath = fullPath + "." + type;

		HttpEntity entity = response.getEntity();
		InputStream input = null;
		try {
			input = entity.getContent();

			File file = new File(fullPath);
			FileOutputStream output = FileUtils.openOutputStream(file);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
			
			EntityUtils.consume(entity);
		} finally {
			IOUtils.closeQuietly(input);
			if (httpget != null) {
				httpget.abort();
			}
		}
	}

	public void close() {
		this.httpclient.get().getConnectionManager().shutdown();
	}

	// 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		// 自定义的恢复策略
		public boolean retryRequest(IOException exception, int executionCount,
				HttpContext context) {
			// 设置恢复策略，在发生异常时候将自动重试5次
			if (executionCount >= 3) {
				// Do not retry if over max retry count
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				return false;
			}
			HttpRequest request = (HttpRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;
		}
	};
	// 使用ResponseHandler接口处理响应，HttpClient使用ResponseHandler会自动管理连接的释放，解决了对连接的释放管理
	private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		// 自定义响应处理
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String charset = EntityUtils.getContentCharSet(entity) == null ? "UTF-8"
						: EntityUtils.getContentCharSet(entity);
				return new String(EntityUtils.toByteArray(entity), charset);
			} else {
				return null;
			}
		}
	};

	/*
	 * //
	 * 使用ResponseHandler接口处理响应，HttpClient使用ResponseHandler会自动管理连接的释放，解决了对连接的释放管理
	 * private static ResponseHandler<byte[]> responseHandlerToByte = new
	 * ResponseHandler<byte[]>() { // 自定义响应处理 public byte[]
	 * handleResponse(HttpResponse response) throws ClientProtocolException,
	 * IOException { StatusLine statusLine = response.getStatusLine(); int
	 * statucode = statusLine.getStatusCode(); if (statucode ==
	 * HttpStatus.SC_MOVED_TEMPORARILY || statucode ==
	 * HttpStatus.SC_MOVED_PERMANENTLY || statucode == HttpStatus.SC_SEE_OTHER
	 * || statucode == HttpStatus.SC_TEMPORARY_REDIRECT) {
	 * System.out.println("Redirect:"); Header header =
	 * response.getFirstHeader("location"); if (header != null) { String newuri
	 * = header.getValue(); if ((newuri == null) || (newuri.equals(""))) newuri
	 * = "/"; System.out.println("To:" + newuri); return null; }
	 * System.out.println("Invalid redirect"); }
	 * 
	 * HttpEntity entity = response.getEntity(); if (entity != null) { return
	 * EntityUtils.toByteArray(entity); } else { return null; } } };
	 */
}
