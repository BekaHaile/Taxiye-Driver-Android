package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 15/03/16.
 */
public class PenalityData {

		public int i;
		public long t;
		public double f;


		public PenalityData(int i, long t, double f){
			this.i = i;
			this.t = t;
			this.f = f;

		}

		@Override
		public String toString() {
			return i+","+t+","+f;
		}

}
