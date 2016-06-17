package product.clicklabs.jugnoo.driver.home;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.home.models.EngagementSPData;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by shankar on 6/15/16.
 */
public class EngagementSP {

	private final String SP_ENGAGEMENTS_ATTACHED = "sp_engagement_attached";
	private final String EMPTY_JSON_ARRAY = "[]";

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
				JSONArray jsonArrayNew = new JSONArray();
				boolean anyCustomerInRide = false;
				for(int i=0; i<jsonArray.length(); i++){
					EngagementSPData engagementSPData = gson.fromJson(jsonArray.getJSONObject(i).toString(), EngagementSPData.class);
					if(engagementSPData.getEngagementId() != customerInfo.getEngagementId()){
						jsonArrayNew.put(jsonArray.getJSONObject(i));
					}
					if(engagementSPData.getStatus() == EngagementStatus.STARTED.getOrdinal()){
						anyCustomerInRide = true;
					}
				}
				if(customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()){
					anyCustomerInRide = true;
				}
				int pathStartId = Integer.MAX_VALUE;
				if(customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()){
					pathStartId = Database2.getInstance(context).getCurrentPathItemsLastId();
				}
				jsonArrayNew.put(gson.toJson(new EngagementSPData(customerInfo.getEngagementId(), pathStartId, customerInfo.getStatus(),
								customerInfo.getRequestlLatLng().latitude, customerInfo.getRequestlLatLng().longitude,
								customerInfo.getUserId(), customerInfo.getReferenceId()),
						EngagementSPData.class));
				Prefs.with(context).save(SP_ENGAGEMENTS_ATTACHED, jsonArrayNew.toString());
				if(anyCustomerInRide){
					Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_IN_RIDE.getOrdinal());
				} else{
					Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_ARRIVED.getOrdinal());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeCustomer(int engagementId){
		try {
			JSONArray jsonArray = new JSONArray(Prefs.with(context).getString(SP_ENGAGEMENTS_ATTACHED, EMPTY_JSON_ARRAY));
			JSONArray jsonArrayNew = new JSONArray();
			for (int i = 0; i < jsonArray.length(); i++) {
				EngagementSPData engagementSPData = gson.fromJson(jsonArray.getJSONObject(i).toString(), EngagementSPData.class);
				if(engagementSPData.getEngagementId() != engagementId){
					jsonArrayNew.put(jsonArray.getInt(i));
				}
			}
			Prefs.with(context).save(SP_ENGAGEMENTS_ATTACHED, jsonArrayNew.toString());
			if(jsonArrayNew.length() == 0){
				Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public ArrayList<EngagementSPData> getAttachedEngagementsData(){
		ArrayList<EngagementSPData> engagementSPDatas = new ArrayList<>();
		try{
			JSONArray jsonArray = new JSONArray(Prefs.with(context).getString(SP_ENGAGEMENTS_ATTACHED, EMPTY_JSON_ARRAY));
			for(int i=0; i<jsonArray.length(); i++){
				engagementSPDatas.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), EngagementSPData.class));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return engagementSPDatas;
	}

	public void updateEngagementSPData(EngagementSPData engagementSPData){
		try{
			JSONArray jsonArray = new JSONArray(Prefs.with(context).getString(SP_ENGAGEMENTS_ATTACHED, EMPTY_JSON_ARRAY));
			JSONArray jsonArrayNew = new JSONArray();
			for (int i = 0; i < jsonArray.length(); i++) {
				EngagementSPData engagementSPDataSaved = gson.fromJson(jsonArray.getJSONObject(i).toString(), EngagementSPData.class);
				if(engagementSPDataSaved.getEngagementId() != engagementSPData.getEngagementId()){
					jsonArrayNew.put(jsonArray.getInt(i));
				} else{
					jsonArrayNew.put(gson.toJson(engagementSPData));
				}
			}
			Prefs.with(context).save(SP_ENGAGEMENTS_ATTACHED, jsonArrayNew.toString());

		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
