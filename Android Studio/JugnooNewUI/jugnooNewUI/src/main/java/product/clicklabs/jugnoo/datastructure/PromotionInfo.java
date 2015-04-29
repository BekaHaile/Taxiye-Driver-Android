package product.clicklabs.jugnoo.datastructure;

public class PromotionInfo extends PromoCoupon{
	
	public String title;
	public String terms;
	
	public PromotionInfo(int id, String title, String terms){
		this.id = id;
		this.title = title;
		this.terms = terms;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if((((PromotionInfo)o).id == this.id)){
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
