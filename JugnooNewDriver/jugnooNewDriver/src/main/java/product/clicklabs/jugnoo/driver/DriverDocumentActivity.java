package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.RelativeLayout;

import rmn.androidscreenlibrary.ASSL;

public class DriverDocumentActivity extends FragmentActivity{


	RelativeLayout relative;
	
	Button backBtn, submitButton;

	RelativeLayout relativeLayoutRides;

	DocumentListFragment documentListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_documents);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(DriverDocumentActivity.this, relative, 1134, 720, false);

		submitButton = (Button) findViewById(R.id.submitButton);

		relativeLayoutRides = (RelativeLayout) findViewById(R.id.relativeLayoutRides);
		documentListFragment = new DocumentListFragment();

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment, documentListFragment, DocumentListFragment.class.getName())
				.addToBackStack(DocumentListFragment.class.getName())
				.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		documentListFragment.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
	
}
