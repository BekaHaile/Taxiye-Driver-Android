package product.clicklabs.jugnoo.driver.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.retrofit.model.DocFieldsInfo;

public class DocInfo implements Parcelable {

	public String docType;
	public Integer docTypeNum, docCount, isEditable;
	public Integer docRequirement;
	public String status, reason;
	public boolean isExpended;
	public ArrayList<String> url;
	private File file, file1;
	private String docInstructions;
	private Integer galleryRestricted;
	private ArrayList<DocFieldsInfo> listDocFieldsInfo;
	private boolean docInfoEditable;



	public DocInfo(String docType, Integer docTypeNum, Integer docRequirement,
				   String status, ArrayList<String> url, String reason, Integer docCount, Integer isEditable,
				   String docInstructions, Integer galleryRestricted, ArrayList<DocFieldsInfo> listDocFieldsInfo,boolean docInfoEditable) {
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
		this.listDocFieldsInfo = listDocFieldsInfo;
		this.docInfoEditable = docInfoEditable;
	}

	protected DocInfo(Parcel in) {
		docType = in.readString();
		if (in.readByte() == 0) {
			docTypeNum = null;
		} else {
			docTypeNum = in.readInt();
		}
		if (in.readByte() == 0) {
			docCount = null;
		} else {
			docCount = in.readInt();
		}
		if (in.readByte() == 0) {
			isEditable = null;
		} else {
			isEditable = in.readInt();
		}
		if (in.readByte() == 0) {
			docRequirement = null;
		} else {
			docRequirement = in.readInt();
		}
		status = in.readString();
		reason = in.readString();
		isExpended = in.readByte() != 0;
		url = in.createStringArrayList();
		docInstructions = in.readString();
		if (in.readByte() == 0) {
			galleryRestricted = null;
		} else {
			galleryRestricted = in.readInt();
		}
		listDocFieldsInfo = in.createTypedArrayList(DocFieldsInfo.CREATOR);
		docInfoEditable = in.readByte() != 0;
	}

	public static final Creator<DocInfo> CREATOR = new Creator<DocInfo>() {
		@Override
		public DocInfo createFromParcel(Parcel in) {
			return new DocInfo(in);
		}

		@Override
		public DocInfo[] newArray(int size) {
			return new DocInfo[size];
		}
	};

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

	public boolean isDocInfoEditable() {
		return docInfoEditable;
	}

	public ArrayList<DocFieldsInfo> getListDocFieldsInfo() {
		return listDocFieldsInfo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(docType);
		if (docTypeNum == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeInt(docTypeNum);
		}
		if (docCount == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeInt(docCount);
		}
		if (isEditable == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeInt(isEditable);
		}
		if (docRequirement == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeInt(docRequirement);
		}
		dest.writeString(status);
		dest.writeString(reason);
		dest.writeByte((byte) (isExpended ? 1 : 0));
		dest.writeStringList(url);
		dest.writeString(docInstructions);
		if (galleryRestricted == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeInt(galleryRestricted);
		}
		dest.writeTypedList(listDocFieldsInfo);
		dest.writeByte((byte) (docInfoEditable ? 1 : 0));
	}
}
