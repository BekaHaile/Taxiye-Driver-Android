package product.clicklabs.jugnoo.datastructure;

public class TransactionInfo {
	
	public int transactionId;
	public String time, date;
	public String transactionType;
	public double amount;
	
	public TransactionInfo(int transactionId, String time, String date, String transactionType, double amount){
		this.transactionId = transactionId;
		this.time = time;
		this.date = date;
		this.transactionType = transactionType;
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
