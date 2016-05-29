package product.clicklabs.jugnoo.driver.pattern;

/**
 * Created by shankar on 5/29/16.
 */
public abstract class State {
	public abstract void receivedRequest();
	public abstract void requestCancelledByCustomer();
	public abstract void requestAcceptedByOtherDriver();
	public abstract void requestTimeout();

	public abstract void acceptRequest();
	public abstract void cancelRequest();



}
