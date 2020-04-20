package product.clicklabs.jugnoo.driver.datastructure;

import android.text.TextUtils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.DocImage;
import product.clicklabs.jugnoo.driver.retrofit.model.DocFieldsInfo;

public class DocInfo {

	public String docType;
	public Integer docTypeNum, docCount, isEditable,docCategory;
	public Integer docRequirement;
	public String status, reason;
	public boolean isExpended;
	private ArrayList<DocImage> docImages;
	private String docInstructions;
	private Integer galleryRestricted;
	private ArrayList<DocFieldsInfo> listDocFieldsInfo;
	private boolean docInfoEditable;



	public DocInfo(String docType, Integer docTypeNum, Integer docRequirement,
				   String status, ArrayList<String> url, String reason, Integer docCount, Integer isEditable,
				   String docInstructions, Integer galleryRestricted, ArrayList<DocFieldsInfo> listDocFieldsInfo,boolean docInfoEditable,int docCategory) {
		this.docType = docType;
		this.docTypeNum = docTypeNum;
		this.docRequirement = docRequirement;
		this.status = status;
		this.reason = reason;
		this.isEditable = isEditable;
		this.isExpended = false;
		this.docCount = docCount;
		this.docCategory=docCategory;

		docImages = new ArrayList<>();
		for(int i=0; i<docCount; i++){
			if(url != null && i < url.size()){
				docImages.add(new DocImage(url.get(i), null));
			} else {
				docImages.add(new DocImage());
			}
		}

		this.docInstructions = docInstructions;

		this.galleryRestricted = galleryRestricted;
		this.listDocFieldsInfo = listDocFieldsInfo;
		this.docInfoEditable = docInfoEditable;
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

	public boolean isDocInfoEditable() {
		return docInfoEditable;
	}

	public ArrayList<DocFieldsInfo> getListDocFieldsInfo() {
		return listDocFieldsInfo;
	}

	public boolean checkIfURLEmpty(){
		for(DocImage docImage : docImages){
			if(!TextUtils.isEmpty(docImage.getImageUrl()) || docImage.getFile() != null){
				return false;
			}
		}
		return true;
	}

	public ArrayList<DocImage> getDocImages(){
		return docImages;
	}

	public Integer getDocCategory() {
		return docCategory;
	}
}
