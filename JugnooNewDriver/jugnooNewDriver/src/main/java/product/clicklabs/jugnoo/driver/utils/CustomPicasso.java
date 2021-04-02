package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import product.clicklabs.jugnoo.driver.BuildConfig;


public class CustomPicasso {

	private static String LOG_TAG = CustomPicasso.class.getSimpleName();
	private static boolean hasCustomPicassoSingletonInstanceSet;

	public static Picasso with(Context context) {

		if (hasCustomPicassoSingletonInstanceSet)
			return Picasso.with(context);

		try {
			Picasso.setSingletonInstance(null);
		} catch (IllegalStateException e) {
			Log.w(LOG_TAG, "-> Default singleton instance already present" +
					" so CustomPicasso singleton cannot be set. Use CustomPicasso.getNewInstance() now.");
			return Picasso.with(context);
		}

		OkHttpClient client = new OkHttpClient.Builder()
				.protocols(Collections.singletonList(Protocol.HTTP_1_1))
				.build();

		Picasso picasso = new Picasso.Builder(context).
				downloader(new OkHttp3Downloader(client))
				.build();
		picasso.setLoggingEnabled(BuildConfig.DEBUG);

		Picasso.setSingletonInstance(picasso);
		Log.w(LOG_TAG, "-> CustomPicasso singleton set to Picasso singleton." +
				" In case if you need Picasso singleton in future then use Picasso.Builder()");
		hasCustomPicassoSingletonInstanceSet = true;

		return picasso;
	}

	public static Picasso getNewInstance(Context context) {

		Log.w(LOG_TAG, "-> Do not forget to call customPicasso.shutdown()" +
				" to avoid memory leak");

		return new Picasso.Builder(context).
				downloader(new OkHttp3Downloader(context))
				.build();
	}
}