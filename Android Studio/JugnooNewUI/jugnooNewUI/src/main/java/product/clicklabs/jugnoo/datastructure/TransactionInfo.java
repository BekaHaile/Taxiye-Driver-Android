package product.clicklabs.jugnoo.datastructure;

public class TransactionInfo {
	
	public int transactionId, transactionType;
	public String time, date;
	public String transactionText;
	public double amount;
	
	public TransactionInfo(int transactionId, int transactionType, String time, String date, String transactionText, double amount){
		this.transactionId = transactionId;
		this.transactionType = transactionType;
		this.time = time;
		this.date = date;
		this.transactionText = transactionText;
		this.amount = amount;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((TransactionInfo)o).transactionId == this.transactionId){
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
