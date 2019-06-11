package product.clicklabs.jugnoo.driver.retrofit.model.drivertaks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Awesome Pojo Generator
 * */
public class Tasks{
  @SerializedName("task_type")
  @Expose
  private int taskType;
  @SerializedName("advertise_credits")
  @Expose
  private double advertiseCredits;
  @SerializedName("start_time")
  @Expose
  private String startTime;
  @SerializedName("end_time")
  @Expose
  private String endTime;

  public int getTaskType() {
    return taskType;
  }

  public void setTaskType(int taskType) {
    this.taskType = taskType;
  }

  public double getAdvertiseCredits() {
    return advertiseCredits;
  }

  public void setAdvertiseCredits(double advertiseCredits) {
    this.advertiseCredits = advertiseCredits;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }
}