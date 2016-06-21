package product.clicklabs.jugnoo.driver.datastructure;

import java.io.File;
import java.util.ArrayList;

public class DocInfo {

	public String docType;
	public Integer docTypeNum;
	public Integer docRequirement;
	public String status;
	public ArrayList<String> url;
	private File file, file1;

	public DocInfo(String docType, Integer docTypeNum, Integer docRequirement,
				   String status, ArrayList<String> url) {
		this.docType = docType;
		this.docTypeNum = docTypeNum;
		this.docRequirement = docRequirement;
		this.status = status;
		this.file = null;
		this.file1 = null;
		this.url = url;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile1() {
		return file1;
	}

	public void setFile1(File file1) {
		this.file1 = file1;
	}
}
