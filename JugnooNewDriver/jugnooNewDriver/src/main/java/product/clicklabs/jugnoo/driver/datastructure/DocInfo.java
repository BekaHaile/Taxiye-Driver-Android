package product.clicklabs.jugnoo.driver.datastructure;

import java.io.File;
import java.util.ArrayList;

public class DocInfo {

	public String docType;
	public Integer docTypeNum, docCount, isEditable;
	public Integer docRequirement;
	public String status, reason;
	public boolean isExpended;
	public ArrayList<String> url;
	private File file, file1;
	private String docInstructions;
	private Integer galleryRestricted;

	public DocInfo(String docType, Integer docTypeNum, Integer docRequirement,
				   String status, ArrayList<String> url, String reason, Integer docCount, Integer isEditable,
				   String docInstructions, Integer galleryRestricted) {
		this.docType = docType;
		this.docTypeNum = docTypeNum;
		this.docRequirement = docRequirement;
		this.status = status;
		this.reason = reason;
		this.file = null;
		this.file1 = null;
		this.isEditable = isEditable;
		this.isExpended = false;
		this.docCount = docCount;
		this.url = url;
		this.docInstructions = docInstructions;
		if(this.url != null) {
			if (this.url.size() == 0) {
				this.url.add(null);
				this.url.add(null);
			} else if (this.url.size() == 1) {
				this.url.add(null);
			}
		}
		this.galleryRestricted = galleryRestricted;
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

	public String getDocInstructions() {
		return docInstructions;
	}

	public Integer getGalleryRestricted() {
		return galleryRestricted;
	}

	public void setGalleryRestricted(Integer galleryRestricted) {
		this.galleryRestricted = galleryRestricted;
	}
}
