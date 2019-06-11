package product.clicklabs.jugnoo.driver.retrofit.model.drivertaks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Awesome Pojo Generator
 * */
public class DriverTasks{
  @SerializedName("flag")
  @Expose
  private int flag;
  @SerializedName("tasks")
  @Expose
  private List<Tasks> tasks;

  public int getFlag() {
    return flag;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }

  public List<Tasks> getTasks() {
    return tasks;
  }

  public void setTasks(List<Tasks> tasks) {
    this.tasks = tasks;
  }
}