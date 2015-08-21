package product.clicklabs.jugnoo.driver.datastructure;

public class FatafatCustomerInfo {
	
	public int userId;
	public String name, phoneNo;
	
	public FatafatCustomerInfo(int userId, String name, String phoneNo){
		this.userId = userId;
		this.name = name;
		this.phoneNo = phoneNo;
	}
	
	@Override
	public String toString() {
		return "userId = "+userId+" name = "+name+" phoneNo = "+phoneNo;
	}
	
}
