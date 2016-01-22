package product.clicklabs.jugnoo.driver.datastructure;

public class DocInfo {

	public String docType;
	public Integer docTypeNum;
	public String docRequirement;
	public String status;



	public DocInfo(String docType, Integer docTypeNum, String docRequirement,
				   String status) {
		this.docType = docType;
		this.docTypeNum = docTypeNum;
		this.docRequirement = docRequirement;
		this.status = status;
	}

}
