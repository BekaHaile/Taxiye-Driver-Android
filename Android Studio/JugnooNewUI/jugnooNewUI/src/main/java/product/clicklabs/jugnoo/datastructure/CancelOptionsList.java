package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

public class CancelOptionsList{
	
	public ArrayList<CancelOption> cancelOptions;
	public String message;
	
	public CancelOptionsList(ArrayList<CancelOption> cancelOptions, String message){
		this.cancelOptions = new ArrayList<CancelOption>();
		this.cancelOptions.addAll(cancelOptions);
		this.message = message;
	}
	
}
