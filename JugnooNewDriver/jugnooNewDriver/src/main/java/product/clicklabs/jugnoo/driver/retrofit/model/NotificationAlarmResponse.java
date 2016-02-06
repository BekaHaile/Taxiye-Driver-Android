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
		@SerializedName("sound_url")
		@Expose
		private String soundUrl;

		/**
		 * @return The soundId
		 */
		public String getSoundId() {
			return soundId;
		}

		/**
		 * @param soundId The sound_id
		 */
		public void setSoundId(String soundId) {
			this.soundId = soundId;
		}

		/**
		 *
		 * @return
		 * The soundUrl
		 */
		public String getSoundUrl() {
			return soundUrl;
		}

		/**
		 *
		 * @param soundUrl
		 * The sound_url
		 */
		public void setSoundUrl(String soundUrl) {
			this.soundUrl = soundUrl;
		}

	}

}
