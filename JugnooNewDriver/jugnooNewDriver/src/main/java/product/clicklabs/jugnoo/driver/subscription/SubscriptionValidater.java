package product.clicklabs.jugnoo.driver.subscription;

/*import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.DocumentListFragment;
import product.clicklabs.jugnoo.driver.R;

public class SubscriptionValidater extends FragmentActivity {

    private SubscriptionFragment subsFrag;
    public boolean checkForSubscribedDriver(String jsonString) {
        try {
            JSONObject jObj = new JSONObject(jsonString);
            JSONObject jLoginObject = jObj.getJSONObject("login");
            if(jLoginObject.has("driver_subscription")){
                if(jLoginObject.getInt("driver_subscription")==1){
                    int key =0;
                    if(jLoginObject.has("stripe_cards_enabled")){
                        key = jLoginObject.getInt("stripe_cards_enabled");
                    }
                    String token = jLoginObject.get("access_token").toString();
                    subsFrag =new  SubscriptionFragment();
                    Bundle args =new Bundle();
                    args.putString("AccessToken", token);
                    args.putInt("stripe_key", key );
                    subsFrag.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment, subsFrag, SubscriptionFragment.class.getName())
                            .hide(getSupportFragmentManager().findFragmentByTag(DocumentListFragment.class.getName()))
                            .addToBackStack(SubscriptionFragment.class.getName())
                            .commit();
                    return false;
                }else{
                    return true;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
}*/
