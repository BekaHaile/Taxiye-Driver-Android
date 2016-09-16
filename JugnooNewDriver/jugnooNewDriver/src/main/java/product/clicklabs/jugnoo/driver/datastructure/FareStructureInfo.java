package product.clicklabs.jugnoo.driver.datastructure;

public class FareStructureInfo {

	public String info;
	public String value;

	public FareStructureInfo(String info, String value){
		this.info = info;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return info+","+value;
	}
	
}
