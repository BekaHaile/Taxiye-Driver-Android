package product.clicklabs.jugnoo.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class DataLoader {

	public HttpResponse secureLoadDataRetry(String url, ArrayList<NameValuePair> nameValuePairs) throws Exception {
		int count = 0;
		while (count <= HttpRequester.RETRY_COUNT) {
			count += 1;
			try {
				HttpResponse response = secureLoadData(url, nameValuePairs);
				/**
				 * if we get here, that means we were successful and we can
				 * stop.
				 */
				return response;
			} catch (Exception e) {
				/**
				 * if we have exhausted our retry limit
				 */
				if (count <= HttpRequester.RETRY_COUNT) {
					/**
					 * we have retries remaining, so log the message and go
					 * again.
					 */
					System.out.println(e.toString());
					Thread.sleep(HttpRequester.SLEEP_BETWEEN_RETRY);
				} else {
					System.out.println("could not succeed with retry...");
					throw e;
				}
			}
		}
		return null;
	}


public HttpResponse secureLoadData(String url, ArrayList<NameValuePair> nameValuePairs)
        throws ClientProtocolException, IOException,
        NoSuchAlgorithmException, KeyManagementException,
        URISyntaxException, KeyStoreException, UnrecoverableKeyException {
	
    SSLContext ctx = SSLContext.getInstance("TLS");
    ctx.init(null, new TrustManager[] { new CustomX509TrustManager() },
            new SecureRandom());

    //Added timeout
    HttpParams httpParameters = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParameters, HttpRequester.TIMEOUT_CONNECTION);
    HttpConnectionParams.setSoTimeout(httpParameters, HttpRequester.TIMEOUT_SOCKET);
    
    HttpClient client = new DefaultHttpClient(httpParameters);

    SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
    ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    ClientConnectionManager ccm = client.getConnectionManager();
    SchemeRegistry sr = ccm.getSchemeRegistry();
    sr.register(new Scheme("https", ssf, 443));
    
    DefaultHttpClient sslClient = new DefaultHttpClient(ccm, client.getParams());
    sslClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(5, true));

    HttpPost post = new HttpPost(new URI(url));
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
    HttpResponse response = sslClient.execute(post);

    return response;
}

}
