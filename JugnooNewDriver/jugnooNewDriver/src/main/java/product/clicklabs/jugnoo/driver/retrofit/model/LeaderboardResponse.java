package product.clicklabs.jugnoo.driver.retrofit.model;

/**
 * Created by socomo on 12/30/15.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class LeaderboardResponse {

    @SerializedName("global")
    @Expose
    private Global global;
    @SerializedName("local")
    @Expose
    private Local local;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("flag")
    @Expose
    private Integer flag;

    /**
     * @return The global
     */
    public Global getGlobal() {
        return global;
    }

    /**
     * @param global The global
     */
    public void setGlobal(Global global) {
        this.global = global;
    }

    /**
     * @return The local
     */
    public Local getLocal() {
        return local;
    }

    /**
     * @param local The local
     */
    public void setLocal(Local local) {
        this.local = local;
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

    public class Daily {

        @SerializedName("ranklist")
        @Expose
        private List<Ranklist> ranklist = new ArrayList<Ranklist>();
        @SerializedName("userinfo")
        @Expose
        private Userinfo userinfo;

        /**
         * @return The ranklist
         */
        public List<Ranklist> getRanklist() {
            return ranklist;
        }

        /**
         * @param ranklist The ranklist
         */
        public void setRanklist(List<Ranklist> ranklist) {
            this.ranklist = ranklist;
        }

        /**
         * @return The userinfo
         */
        public Userinfo getUserinfo() {
            return userinfo;
        }

        /**
         * @param userinfo The userinfo
         */
        public void setUserinfo(Userinfo userinfo) {
            this.userinfo = userinfo;
        }

    }

    public class Global {

        @SerializedName("daily")
        @Expose
        private Daily daily;
        @SerializedName("weekly")
        @Expose
        private Weekly weekly;

        /**
         * @return The daily
         */
        public Daily getDaily() {
            return daily;
        }

        /**
         * @param daily The daily
         */
        public void setDaily(Daily daily) {
            this.daily = daily;
        }

        /**
         * @return The weekly
         */
        public Weekly getWeekly() {
            return weekly;
        }

        /**
         * @param weekly The weekly
         */
        public void setWeekly(Weekly weekly) {
            this.weekly = weekly;
        }

    }


    public class Local {

        @SerializedName("daily")
        @Expose
        private Daily daily;
        @SerializedName("weekly")
        @Expose
        private Weekly weekly;

        /**
         * @return The daily
         */
        public Daily getDaily() {
            return daily;
        }

        /**
         * @param daily The daily
         */
        public void setDaily(Daily daily) {
            this.daily = daily;
        }

        /**
         * @return The weekly
         */
        public Weekly getWeekly() {
            return weekly;
        }

        /**
         * @param weekly The weekly
         */
        public void setWeekly(Weekly weekly) {
            this.weekly = weekly;
        }

    }


    public class Userinfo {

        @SerializedName("rank")
        @Expose
        private Integer rank;
        @SerializedName("downloads")
        @Expose
        private Integer downloads;

        /**
         * @return The rank
         */
        public Integer getRank() {
            return rank;
        }

        /**
         * @param rank The rank
         */
        public void setRank(Integer rank) {
            this.rank = rank;
        }

        /**
         * @return The downloads
         */
        public Integer getDownloads() {
            return downloads;
        }

        /**
         * @param downloads The downloads
         */
        public void setDownloads(Integer downloads) {
            this.downloads = downloads;
        }

    }

    public class Weekly {

        @SerializedName("ranklist")
        @Expose
        private List<Ranklist> ranklist = new ArrayList<Ranklist>();
        @SerializedName("userinfo")
        @Expose
        private Userinfo userinfo;

        /**
         * @return The ranklist
         */
        public List<Ranklist> getRanklist() {
            return ranklist;
        }

        /**
         * @param ranklist The ranklist
         */
        public void setRanklist(List<Ranklist> ranklist) {
            this.ranklist = ranklist;
        }

        /**
         * @return The userinfo
         */
        public Userinfo getUserinfo() {
            return userinfo;
        }

        /**
         * @param userinfo The userinfo
         */
        public void setUserinfo(Userinfo userinfo) {
            this.userinfo = userinfo;
        }

    }
}

