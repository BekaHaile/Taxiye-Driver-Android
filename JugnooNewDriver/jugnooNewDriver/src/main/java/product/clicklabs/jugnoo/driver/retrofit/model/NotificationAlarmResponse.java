package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 24/11/15.
 */

public class NotificationAlarmResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("regions")
	@Expose
	private List<Sound> sound = new ArrayList<Sound>();

	/**
	 * @return The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 * @param flag The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 * @return The sound
	 */
	public List<Sound> getSound() {
		return sound;
	}

	/**
	 * @param sound The sound
	 */
	public void setSound(List<Sound> sound) {
		this.sound = sound;
	}

	public class Sound {

		@SerializedName("sound_id")
		@Expose
		private String soundId;
		@SerializedName("sound_name")
		@Expose
		private String soundName;

		/**
		 * @return The soundId
		 */
		public String getSoundId() {
			return soundId;
		}

		/**
		 * @param soundId The region_id
		 */
		public void setSoundId(String soundId) {
			this.soundId = soundId;
		}

		/**
		 *
		 * @return
		 * The soundName
		 */
		public String getSoundName() {
			return soundName;
		}

		/**
		 *
		 * @param soundName
		 * The sound_name
		 */
		public void setSoundName(String soundName) {
			this.soundName = soundName;
		}

	}

}
