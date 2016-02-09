package product.clicklabs.jugnoo.driver;

/**
 * Created by socomo20 on 8/18/15.
 */
import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService {

	private static final String TAG = MyInstanceIDListenerService.class.getSimpleName();

	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. This call is initiated by the
	 * InstanceID provider.
	 */
	// [START refresh_token]
	@Override
	public void onTokenRefresh() {
		// Fetch updated Instance ID token and notify our app's server of any changes (if applicable).

	}
	// [END refresh_token]
}