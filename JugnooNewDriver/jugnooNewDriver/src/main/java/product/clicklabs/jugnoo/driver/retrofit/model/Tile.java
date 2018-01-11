package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;

/**
 * Created by gurmail on 09/10/17.
 */
public class Tile {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("text_view_1")
    @Expose
    private String textView1;
    @SerializedName("text_view_2")
    @Expose
    private String textView2;
    @SerializedName("text_view_sub")
    @Expose
    private String textViewSub;
    @SerializedName("deep_index")
    @Expose
    private Integer deepIndex;
    @SerializedName("text_value_1")
    @Expose
    private String textValue1;
    @SerializedName("text_value_2")
    @Expose
    private String textValue2;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("extras")
    @Expose
    private Extras extras;

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The textView1
     */
    public String getTextView1() {
        return textView1;
    }

    /**
     * @param textView1 The text_view_1
     */
    public void setTextView1(String textView1) {
        this.textView1 = textView1;
    }

    /**
     * @return The textView2
     */
    public String getTextView2() {
        return textView2;
    }

    /**
     * @param textView2 The text_view_2
     */
    public void setTextView2(String textView2) {
        this.textView2 = textView2;
    }

    /**
     * @return The textViewSub
     */
    public String getTextViewSub() {
        return textViewSub;
    }

    /**
     * @param textViewSub The text_view_sub
     */
    public void setTextViewSub(String textViewSub) {
        this.textViewSub = textViewSub;
    }

    /**
     * @return The deepIndex
     */
    public Integer getDeepIndex() {
        return deepIndex;
    }

    /**
     * @param deepIndex The deep_index
     */
    public void setDeepIndex(Integer deepIndex) {
        this.deepIndex = deepIndex;
    }

    /**
     * @return The textValue1
     */
    public String getTextValue1() {
        return textValue1;
    }

    /**
     * @param textValue1 The text_value_1
     */
    public void setTextValue1(String textValue1) {
        this.textValue1 = textValue1;
    }

    /**
     * @return The textValue2
     */
    public String getTextValue2() {
        return textValue2;
    }

    /**
     * @param textValue2 The text_value_2
     */
    public void setTextValue2(String textValue2) {
        this.textValue2 = textValue2;
    }

    /**
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return The extras
     */
    public Extras getExtras() {
        return extras;
    }

    /**
     * @param extras The extras
     */
    public void setExtras(Extras extras) {
        this.extras = extras;
    }

    public class Extras {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("engagement_id")
        @Expose
        private Integer engagementId;
        @SerializedName("time")
        @Expose
        private String time;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("ride_param")
        @Expose
        private List<FareStructureInfo> rideParam = new ArrayList<FareStructureInfo>();
        @SerializedName("ride_fare")
        @Expose
        private Double rideFare;
        @SerializedName("earning")
        @Expose
        private Double earning;
        @SerializedName("paid_using_cash")
        @Expose
        private Double paidUsingCash;
        @SerializedName("account")
        @Expose
        private Double account;
        @SerializedName("from")
        @Expose
        private String from;
        @SerializedName("to")
        @Expose
        private List<String> to = new ArrayList<String>();
        @SerializedName("pickup_latitude")
        @Expose
        private Double pickupLatitude;
        @SerializedName("pickup_longitude")
        @Expose
        private Double pickupLongitude;
        @SerializedName("drop_coordinates")
        @Expose
        private List<Extras.DropCoordinate> dropCoordinates = new ArrayList<Extras.DropCoordinate>();
        @SerializedName("ride_time")
        @Expose
        private Integer rideTime;
        @SerializedName("wait_time")
        @Expose
        private Integer waitTime;
        @SerializedName("distance")
        @Expose
        private double distance;
        @SerializedName("redirect_url")
        @Expose
        private String redirectUrl;
        @SerializedName("ticket_status")
        @Expose
        private Integer ticketStatus;
        @SerializedName("ticket_date")
        @Expose
        private String ticketDate;
        @SerializedName("slots")
        @Expose
        private ArrayList<CaptiveSlots> slots;

        /**
         * @return The type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type The type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return The engagementId
         */
        public Integer getEngagementId() {
            return engagementId;
        }

        /**
         * @param engagementId The engagement_id
         */
        public void setEngagementId(Integer engagementId) {
            this.engagementId = engagementId;
        }


        /**
         * @return The time
         */
        public String getTime() {
            return time;
        }

        /**
         * @param time The time
         */
        public void setTime(String time) {
            this.time = time;
        }


        /**
         * @return The date
         */
        public String getDate() {
            return date;
        }

        /**
         * @param date The date
         */
        public void setDate(String date) {
            this.date = date;
        }

        /**
         * @return The rideFare
         */
        public Double getRideFare() {
            return rideFare;
        }

        /**
         * @param rideFare The ride_fare
         */
        public void setRideFare(Double rideFare) {
            this.rideFare = rideFare;
        }

        /**
         * @return The status
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status The status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * @return The rideParam
         */
        public List<FareStructureInfo> getRideParam() {
            return rideParam;
        }

        /**
         * @param rideParam The ride_param
         */
        public void setRideParam(List<FareStructureInfo> rideParam) {
            this.rideParam = rideParam;
        }

        /**
         * @return The earning
         */
        public Double getEarning() {
            return earning;
        }

        /**
         * @param earning The earning
         */
        public void setEarning(Double earning) {
            this.earning = earning;
        }

        /**
         * @return The paidUsingCash
         */
        public Double getPaidUsingCash() {
            return paidUsingCash;
        }

        /**
         * @param paidUsingCash The paid_using_cash
         */
        public void setPaidUsingCash(Double paidUsingCash) {
            this.paidUsingCash = paidUsingCash;
        }

        /**
         * @return The account
         */
        public Double getAccount() {
            return account;
        }

        /**
         * @param account The account
         */
        public void setAccount(Double account) {
            this.account = account;
        }

        /**
         * @return The from
         */
        public String getFrom() {
            return from;
        }

        /**
         * @param from The from
         */
        public void setFrom(String from) {
            this.from = from;
        }

        /**
         * @return The to
         */
        public List<String> getTo() {
            return to;
        }

        /**
         * @param to The to
         */
        public void setTo(List<String> to) {
            this.to = to;
        }

        /**
         * @return The pickupLatitude
         */
        public Double getPickupLatitude() {
            return pickupLatitude;
        }

        /**
         * @param pickupLatitude The pickup_latitude
         */
        public void setPickupLatitude(Double pickupLatitude) {
            this.pickupLatitude = pickupLatitude;
        }

        /**
         * @return The pickupLongitude
         */
        public Double getPickupLongitude() {
            return pickupLongitude;
        }

        /**
         * @param pickupLongitude The pickup_longitude
         */
        public void setPickupLongitude(Double pickupLongitude) {
            this.pickupLongitude = pickupLongitude;
        }

        /**
         * @return The dropCoordinates
         */
        public List<Extras.DropCoordinate> getDropCoordinates() {
            return dropCoordinates;
        }

        /**
         * @param dropCoordinates The drop_coordinates
         */
        public void setDropCoordinates(List<Extras.DropCoordinate> dropCoordinates) {
            this.dropCoordinates = dropCoordinates;
        }


        /**
         * @return The rideTime
         */
        public Integer getRideTime() {
            return rideTime;
        }

        /**
         * @param rideTime The ride_time
         */
        public void setRideTime(Integer rideTime) {
            this.rideTime = rideTime;
        }

        public Integer getWaitTime() {
            return waitTime;
        }

        public void setWaitTime(Integer waitTime) {
            this.waitTime = waitTime;
        }

        /**
         * @return The distance
         */
        public double getDistance() {
            return distance;
        }

        /**
         * @param distance The distance
         */
        public void setDistance(double distance) {
            this.distance = distance;
        }

        /**
         * @return The redirectUrl
         */
        public String getRedirectUrl() {
            return redirectUrl;
        }

        /**
         * @param redirectUrl The redirect_url
         */
        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public Integer getTicketStatus() {
            return ticketStatus;
        }

        public void setTicketStatus(Integer ticketStatus) {
            this.ticketStatus = ticketStatus;
        }

        public String getTicketDate() {
            return ticketDate;
        }

        public void setTicketDate(String ticketDate) {
            this.ticketDate = ticketDate;
        }

        public class DropCoordinate {

            @SerializedName("latitude")
            @Expose
            private Double latitude;
            @SerializedName("longitude")
            @Expose
            private Double longitude;

            /**
             * @return The latitude
             */
            public Double getLatitude() {
                return latitude;
            }

            /**
             * @param latitude The latitude
             */
            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }

            /**
             * @return The longitude
             */
            public Double getLongitude() {
                return longitude;
            }

            /**
             * @param longitude The longitude
             */
            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }

        }

    }

}
