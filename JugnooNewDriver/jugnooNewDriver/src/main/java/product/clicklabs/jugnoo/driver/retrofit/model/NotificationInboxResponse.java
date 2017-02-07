package product.clicklabs.jugnoo.driver.retrofit.model;

/**
 * Created by socomo33 on 3/28/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
public class NotificationInboxResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("pushes")
    @Expose
    public List<NotificationData> pushes = new ArrayList<NotificationData>();
    @SerializedName("total")
    @Expose
    private Integer total;

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
     * @return The pushes
     */
    public List<NotificationData> getPushes() {
        return pushes;
    }

    /**
     * @param pushes The pushes
     */
    public void setPushes(List<NotificationData> pushes) {
        this.pushes = pushes;
    }

    /**
     * @return The total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * @param total The total
     */
    public void setTotal(Integer total) {
        this.total = total;
    }


    public class NotificationData {

        @SerializedName("notification_id")
        @Expose
        private int notificationId;
        @SerializedName("timePushArrived")
        @Expose
        private String timePushArrived;
        @SerializedName("counterTime")
        @Expose
        private String title;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("deepindex")
        @Expose
        private int deepIndex;
        @SerializedName("timeToDisplay")
        @Expose
        private String timeToDisplay;
        @SerializedName("timeTillDisplay")
        @Expose
        private String timeTillDisplay;
        @SerializedName("image")
        @Expose
        private String notificationImage;

        public NotificationData(int notificationId, String timePushArrived, String title, String message, int deepIndex, String timeToDisplay, String timeTillDisplay, String notificationImage) {
            this.notificationId = notificationId;
            this.timePushArrived = timePushArrived;
            this.title = title;
            this.message = message;
            this.deepIndex = deepIndex;
            this.timeToDisplay = timeToDisplay;
            this.timeTillDisplay = timeTillDisplay;
            this.notificationImage = notificationImage;
        }

        public int getNotificationId() {
            return notificationId;
        }

        public void setNotificationId(int notificationId) {
            this.notificationId = notificationId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getDeepIndex() {
            return deepIndex;
        }

        public void setDeepIndex(int deepIndex) {
            this.deepIndex = deepIndex;
        }

        public String getTimeToDisplay() {
            return timeToDisplay;
        }

        public void setTimeToDisplay(String timeToDisplay) {
            this.timeToDisplay = timeToDisplay;
        }

        public String getTimeTillDisplay() {
            return timeTillDisplay;
        }

        public void setTimeTillDisplay(String timeTillDisplay) {
            this.timeTillDisplay = timeTillDisplay;
        }

        public String getTimePushArrived() {
            return timePushArrived;
        }

        public void setTimePushArrived(String timePushArrived) {
            this.timePushArrived = timePushArrived;
        }

        public String getNotificationImage() {
            return notificationImage;
        }

        public void setNotificationImage(String notificationImage) {
            this.notificationImage = notificationImage;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}