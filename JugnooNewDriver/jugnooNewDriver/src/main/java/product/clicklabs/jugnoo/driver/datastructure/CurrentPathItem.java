package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by socomo20 on 6/12/15.
 */
public class CurrentPathItem {

    public long id;
    public long parentId;
    public LatLng sLatLng;
    public LatLng dLatLng;
    public int sectionIncomplete;
    public int googlePath;
    public int acknowledged;

    public CurrentPathItem(long id, long parentId, double slat, double slng, double dlat, double dlng, int sectionIncomplete, int googlePath, int acknowledged){
        this.id = id;
        this.parentId = parentId;
        this.sLatLng = new LatLng(slat, slng);
        this.dLatLng = new LatLng(dlat, dlng);
        this.sectionIncomplete = sectionIncomplete;
        this.googlePath = googlePath;
        this.acknowledged = acknowledged;
    }

    @Override
    public boolean equals(Object o) {
        try{
            if(((CurrentPathItem)o).id == this.id){
                return true;
            }
            else{
                return false;
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return id+","+parentId+","+sectionIncomplete+","+googlePath+","+acknowledged;
    }
}
