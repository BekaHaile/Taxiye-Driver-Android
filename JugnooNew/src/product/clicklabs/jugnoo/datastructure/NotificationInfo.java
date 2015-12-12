package product.clicklabs.jugnoo.datastructure;

public class NotificationInfo {
	
	public String id;
	public String title;
	
	public NotificationInfo(String id, String title){
		this.id = id;
		this.title = title;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if((((NotificationInfo)o).id == this.id)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}
	
}
