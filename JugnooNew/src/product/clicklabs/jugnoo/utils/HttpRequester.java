package product.clicklabs.jugnoo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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

import android.os.Environment;
import android.util.Log;

public class HttpRequester {

	public static String SERVER_TIMEOUT = "SERVER_TIMEOUT";
	static int TIMEOUT_CONNECTION = 30000, TIMEOUT_SOCKET = 30000, RETRY_COUNT = 0, SLEEP_BETWEEN_RETRY = 0;

	// constructor
	public HttpRequester() {
		SERVER_TIMEOUT = "SERVER_TIMEOUT";
	}

	public String getJSONFromUrl(String url) {
		InputStream inputStream = null;
		String json = "";
		// Making HTTP request
		try {
			// defaultHttpClient
			//Added timeout
	        HttpParams httpParameters = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParameters, HttpRequester.TIMEOUT_CONNECTION);
	        HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
	        
	        HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (inputStream != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder stringBuilder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line + "\n");
				}

				json = stringBuilder.toString();
				inputStream.close();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
			json = SERVER_TIMEOUT + " " +e;
		}
		return json;

	}


	public void writeJSONToFile(String response, String filePrefix) {
		try {
			String fileName = Environment.getExternalStorageDirectory() + "/"
					+ filePrefix + ".txt";

			File ff = new File(fileName);
			ff.delete();

			File gpxfile = new File(fileName);
			FileWriter writer = new FileWriter(gpxfile);
			writer.append("" + response);
			writer.flush();
			writer.close();
		} catch (Exception e1) {
			Log.e("file exception", "=-=" + e1.toString());
			e1.printStackTrace();
		}
	}
	
	
	
	public String getJSONFromUrlParams(String url, ArrayList<NameValuePair> nameValuePairs){
        try {
            DataLoader dl = new DataLoader();
            HttpResponse response = dl.secureLoadDataRetry(url, nameValuePairs); 

            StringBuilder sb = new StringBuilder();
            sb.append("HEADERS:\n\n");

            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                Header h = headers[i];
                sb.append(h.getName()).append(":\t").append(h.getValue()).append("\n");
            }

            InputStream is = response.getEntity().getContent();
            StringBuilder out = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (String line = br.readLine(); line != null; line = br.readLine())
                out.append(line);
            br.close();

            sb.append("\n\nCONTENT:\n\n").append(out.toString()); 

            return out.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return SERVER_TIMEOUT + " " +e;
        }
    }
	


/**
 * Taken from: http://janis.peisenieks.lv/en/76/english-making-an-ssl-connection-via-android/
 *
 */
class CustomSSLSocketFactory extends SSLSocketFactory {
    SSLContext sslContext = SSLContext.getInstance("TLS");

    public CustomSSLSocketFactory(KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        TrustManager tm = new CustomX509TrustManager();

        sslContext.init(null, new TrustManager[] { tm }, null);
    }

    public CustomSSLSocketFactory(SSLContext context)
            throws KeyManagementException, NoSuchAlgorithmException,
            KeyStoreException, UnrecoverableKeyException {
        super(null);
        sslContext = context;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port,
                autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}



class CustomX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
            String authType) throws CertificateException {

        // Here you can verify the servers certificate. (e.g. against one which is stored on mobile device)

        // InputStream inStream = null;
        // try {
        // inStream = MeaApplication.loadCertAsInputStream();
        // CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // X509Certificate ca = (X509Certificate)
        // cf.generateCertificate(inStream);
        // inStream.close();
        //
        // for (X509Certificate cert : certs) {
        // // Verifing by public key
        // cert.verify(ca.getPublicKey());
        // }
        // } catch (Exception e) {
        // throw new IllegalArgumentException("Untrusted Certificate!");
        // } finally {
        // try {
        // inStream.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}



class DataLoader {

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
        HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
        
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

	
}








