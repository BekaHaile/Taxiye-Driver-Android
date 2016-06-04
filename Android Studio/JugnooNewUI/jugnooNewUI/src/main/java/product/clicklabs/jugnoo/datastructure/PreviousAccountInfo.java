package product.clicklabs.jugnoo.datastructure;

public class PreviousAccountInfo {

    public int userId;
    public String userName, userEmail, phoneNo, dateRegistered;

	public PreviousAccountInfo(int userId, String userName, String userEmail, String phoneNo, String dateRegistered){
		this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.phoneNo = phoneNo;
        this.dateRegistered = dateRegistered;
	}

}
