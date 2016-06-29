package product.clicklabs.jugnoo.driver.dodo.datastructure;

public class DeliveryReturnOption {
	private int id;
	private String name;
	private boolean checked;

	public DeliveryReturnOption(String name){
		this.id = 0;
		this.name = name;
		this.checked = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
