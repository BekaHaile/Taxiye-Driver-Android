package product.clicklabs.jugnoo.driver.home;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.home.models.EngagementSPData;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by shankar on 6/15/16.
 */
public class EngagementSP {

	private final String SP_ENGAGEMENTS_ATTACHED = "sp_engagement_ids_attached";
	private final String SP_ENGAGEMENT_DATA_SUFFIX = "_espd";
	private final String EMPTY_JSON_ARRAY = "[]";
	private final String EMPTY_JSON_OBJECT = "{}";

	private Context context;
	private Gson gson;

	public EngagementSP(Context context){
		this.context = context;
		this.gson = new Gson();
	}

	public void addCustomer(CustomerInfo customerInfo){
		try {
			if(customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
					|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()
					|| customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
				JSONArray jsonArray = new JSONArray(Prefs.with(context).getString(SP_ENGAGEMENTS_ATTACHED, EMPTY_JSON_ARRAY));
				boolean alreadyAdded = false;
				for(int i=0; i<jsonArray.length(); i++){
					if(jsonArray.getInt(i) == customerInfo.getEngagementId()){
						alreadyAdded = true;
						break;
					}
				}
				if(!alreadyAdded) {
					jsonArray.put(customerInfo.getEngagementId());
					Prefs.with(context).save(SP_ENGAGEMENTS_ATTACHED, jsonArray.toString());
				}
				int pathStartId = Integer.MAX_VALUE;
				if(customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()){
					pathStartId = Database2.getInstance(context).getCurrentPathItemsLastId();
				}
				Prefs.with(context).save(String.valueOf(customerInfo.getEngagementId())+SP_ENGAGEMENT_DATA_SUFFIX,
						gson.toJson(new EngagementSPData(pathStartId, customerInfo.getStatus(),
										customerInfo.getRequestlLatLng().latitude, customerInfo.getRequestlLatLng().longitude,
										customerInfo.getUserId(), customerInfo.getReferenceId()),
								EngagementSPData.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public ArrayList<Integer> getAttachedEngagements(){
		ArrayList<Integer> engagementIds = new ArrayList<>();
		try{
			JSONArray jsonArray = new JSONArray(Prefs.with(context).getString(SP_ENGAGEMENTS_ATTACHED, EMPTY_JSON_ARRAY));
			for(int i=0; i<jsonArray.length(); i++){
				engagementIds.add(jsonArray.getInt(i));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return engagementIds;
	}


	public EngagementSPData getEngagementSPData(int engagementId){
		try{
			return gson.fromJson(Prefs.with(context).getString(String.valueOf(engagementId)+SP_ENGAGEMENT_DATA_SUFFIX,
							EMPTY_JSON_OBJECT),
					EngagementSPData.class);
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public void updateEngagementSPData(int engagementId, EngagementSPData engagementSPData){
		try{
			Prefs.with(context).save(String.valueOf(engagementId)+SP_ENGAGEMENT_DATA_SUFFIX,
					gson.toJson(engagementSPData, EngagementSPData.class));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
