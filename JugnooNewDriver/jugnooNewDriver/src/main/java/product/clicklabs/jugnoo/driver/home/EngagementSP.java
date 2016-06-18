package product.clicklabs.jugnoo.driver.home;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

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
				List<EngagementSPData> engagementSPDatas = getEngagementSPDatasFromPrefs();

				boolean anyCustomerInRide = false;
				for(EngagementSPData engagementSPData : engagementSPDatas){
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

				EngagementSPData engagementSPData = new EngagementSPData(customerInfo.getEngagementId(), pathStartId, customerInfo.getStatus(),
						customerInfo.getRequestlLatLng().latitude, customerInfo.getRequestlLatLng().longitude,
						customerInfo.getUserId(), customerInfo.getReferenceId());

				if(engagementSPDatas.contains(engagementSPData)){
					engagementSPDatas.set(engagementSPDatas.indexOf(engagementSPData), engagementSPData);
				} else{
					engagementSPDatas.add(engagementSPData);
				}

				saveEngagementSPDatasToPrefs(engagementSPDatas);
				if(anyCustomerInRide){
					Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_IN_RIDE.getOrdinal());
				} else{
					Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_ARRIVED.getOrdinal());
				}
			} else {
				removeCustomer(customerInfo.getEngagementId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeCustomer(int engagementId){
		try {
			List<EngagementSPData> engagementSPDatas = getEngagementSPDatasFromPrefs();
			engagementSPDatas.remove(new EngagementSPData(engagementId));
			saveEngagementSPDatasToPrefs(engagementSPDatas);

			if(engagementSPDatas.size() == 0){
				Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public ArrayList<EngagementSPData> getAttachedEngagementsData(){
		return (ArrayList<EngagementSPData>) getEngagementSPDatasFromPrefs();
	}

	public void updateEngagementSPData(EngagementSPData engagementSPData){
		try{
			List<EngagementSPData> engagementSPDatas = getEngagementSPDatasFromPrefs();
			if(engagementSPDatas.contains(engagementSPData)){
				engagementSPDatas.set(engagementSPDatas.indexOf(engagementSPData), engagementSPData);
			}
			saveEngagementSPDatasToPrefs(engagementSPDatas);
		} catch (Exception e){
			e.printStackTrace();
		}
	}


	private List<EngagementSPData> getEngagementSPDatasFromPrefs(){
		List<EngagementSPData> engagementSPDatas;
		try{
			engagementSPDatas = gson.fromJson(Prefs.with(context)
							.getString(SP_ENGAGEMENTS_ATTACHED, EMPTY_JSON_ARRAY),
					new TypeToken<List<EngagementSPData>>(){}.getType());
		} catch (Exception e){
			e.printStackTrace();
			engagementSPDatas = new ArrayList<>();
		}
		return engagementSPDatas;
	}

	private void saveEngagementSPDatasToPrefs(List<EngagementSPData> engagementSPDatas){
		try{
			JsonElement element = gson.toJsonTree(engagementSPDatas,
					new TypeToken<List<EngagementSPData>>() {}.getType());
			if (!element.isJsonArray()) {
				throw new Exception();
			}
			JsonArray jsonArray = element.getAsJsonArray();
			Prefs.with(context).save(SP_ENGAGEMENTS_ATTACHED, jsonArray.toString());
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
