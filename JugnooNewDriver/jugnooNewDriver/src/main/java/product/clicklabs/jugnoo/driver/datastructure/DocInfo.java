package product.clicklabs.jugnoo.driver.datastructure;

import java.io.File;

public class DocInfo {

	public String docType;
	public Integer docTypeNum;
	public String docRequirement;
	public String status;
	private File file;

	public DocInfo(String docType, Integer docTypeNum, String docRequirement,
				   String status) {
		this.docType = docType;
		this.docTypeNum = docTypeNum;
		this.docRequirement = docRequirement;
		this.status = status;
		this.file = null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
