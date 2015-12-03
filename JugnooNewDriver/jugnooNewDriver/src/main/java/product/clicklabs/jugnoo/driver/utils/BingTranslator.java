package product.clicklabs.jugnoo.driver.utils;

import android.os.AsyncTask;

import com.google.api.GoogleAPI;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;


/**
 * Created by aneeshbansal on 16/11/15.
 */
public class BingTranslator {

	public BingTranslator(){
	}

	public void startTranslation(String text, BingCallback callback){
		new TranslateAsync(callback).execute(text);
	}

	public interface BingCallback{
		void onSuccess(String translatedStr);
		void onFailure();
	}

	private class TranslateAsync extends AsyncTask<String, String, String>{
		private BingCallback callback;
		public TranslateAsync(BingCallback callback){
			this.callback = callback;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String translatedStr = translate(params[0]);
			return translatedStr;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if(s == null){
				callback.onFailure();
			}
			else{
				callback.onSuccess(s);
			}
		}
	}

//	private String translate(String text){
//		// Set the Client ID / Client Secret once per JVM. It is set statically and applies to all services
//		try {
//			Translate.setClientId("aneesh_jugnoo");
//			Translate.setClientSecret("wzZGiM3+xESz/ssyCAZLakFuAb5o7sDbAr3N0dpRX3g=");
//
//			String translatedText = "";
//
//			// English AUTO_DETECT -> gERMAN Change this if u wanna other languages
////			translatedText = Translate.execute(text, Language.GERMAN);
//			translatedText = Translate.execute(text, Language.ENGLISH,
//					Language.TURKISH);
//			return translatedText;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	private String translate(String text){
		// Set the Client ID / Client Secret once per JVM. It is set statically and applies to all services
		try {
			String translatedText = "";
			GoogleAPI.setHttpReferrer("https://www.jugnoo.in");

			// Set the Google Translate API key
			// See: http://code.google.com/apis/language/translate/v2/getting_started.html
			GoogleAPI.setKey("AIzaSyA5eOUeuJ8jotmTZBCOBHRKCp-FLGDZ0KM");

			 translatedText = Translate.DEFAULT.execute(text, Language.ENGLISH, Language.HINDI);

			return translatedText;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}




}
