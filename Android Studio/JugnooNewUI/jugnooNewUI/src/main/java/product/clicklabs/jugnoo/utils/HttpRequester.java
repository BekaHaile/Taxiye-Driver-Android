package product.clicklabs.jugnoo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Environment;
import android.util.Log;

public class HttpRequester {

	public static String SERVER_TIMEOUT = "SERVER_TIMEOUT";
	public static int TIMEOUT_CONNECTION = 30000, TIMEOUT_SOCKET = 30000, RETRY_COUNT = 0, SLEEP_BETWEEN_RETRY = 0;

	// constructor
	public HttpRequester() {
		SERVER_TIMEOUT = "SERVER_TIMEOUT";
	}

	
	public String getJSONFromUrl(String url){
		
		return Config.getHttpRequester().getJSONFromUrlFinal(url);
	}
	
	
	public String getJSONFromUrlParams(String url, ArrayList<NameValuePair> nameValuePairs){
		
		return Config.getHttpRequester().getJSONFromUrlParamsFinal(url, nameValuePairs);
	}

	
}



class HttpRequesterFinal{
	public String getJSONFromUrlFinal(String url) {
		InputStream inputStream = null;
		String json = "";
		// Making HTTP request
		try {
			// defaultHttpClient
			//Added timeout
	        HttpParams httpParameters = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParameters, HttpRequester.TIMEOUT_CONNECTION);
	        HttpConnectionParams.setSoTimeout(httpParameters, HttpRequester.TIMEOUT_SOCKET);
	        
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
			json = HttpRequester.SERVER_TIMEOUT + " " +e;
		}
		return json;

	}

	
	public String getJSONFromUrlParamsFinal(String url, ArrayList<NameValuePair> nameValuePairs){
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
            return HttpRequester.SERVER_TIMEOUT + " " +e;
        }
    }
	
	
	public static void writeJSONToFile(String response, String filePrefix) {
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
}

