package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 30/04/16.
 */
public class AllNotificationData {

	private int notificationId;
	private String notificationPackage;
	private String message;
	private String title;

	public AllNotificationData(int notificationId, String notificationPackage, String message, String title) {
		this.notificationId = notificationId;
		this.notificationPackage = notificationPackage;
		this.message = message;
		this.title = title;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getNotificationPackage() {
		return notificationPackage;
	}

	public void setNotificationPackage(String notificationPackage) {
		this.notificationPackage = notificationPackage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
