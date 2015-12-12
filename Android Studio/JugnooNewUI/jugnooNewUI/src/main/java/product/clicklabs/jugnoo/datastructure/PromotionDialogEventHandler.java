package product.clicklabs.jugnoo.datastructure;

import product.clicklabs.jugnoo.PromotionDialog;

public interface PromotionDialogEventHandler {
	public void onOkPressed(PromoCoupon promoCoupon, int totalPromoCoupons);
	public void onOkOnlyPressed(PromotionDialog promotionDialog, PromoCoupon promoCoupon, String pickupId);
	public void onCancelPressed();
}
