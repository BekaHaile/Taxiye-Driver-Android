package product.clicklabs.jugnoo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import product.clicklabs.jugnoo.datastructure.CouponInfo;

public class DateComparator implements Comparator<CouponInfo>{

	@Override
	public int compare(CouponInfo lhs, CouponInfo rhs) {
		try{
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int i = format.parse(lhs.expiryDate).compareTo(format.parse(rhs.expiryDate));
            return i;
        }
        catch (Exception e){
        	e.printStackTrace();
            return 0;
        }
	}
}
