package product.clicklabs.jugnoo.driver.datastructure;

import java.io.File;

public class DocInfo {

	public String docType;
	public Integer docTypeNum;
	public String docRequirement;
	public String status;
	public String url;
	private File file;

	public DocInfo(String docType, Integer docTypeNum, String docRequirement,
				   String status, String url) {
		this.docType = docType;
		this.docTypeNum = docTypeNum;
		this.docRequirement = docRequirement;
		this.status = status;
		this.file = null;
		this.url = url;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
