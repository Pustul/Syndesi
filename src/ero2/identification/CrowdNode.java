package ero2.identification;

public class CrowdNode {
    private String mNID;
    private NodeType mType;
    private String mStatus;
    private Long LastChangeTS;

    public CrowdNode(String NID, NodeType type, String status){
        this.mNID = NID;
        this.mType = type;
        this.mStatus = status;
    }

    public String getmNID() {
        return mNID;
    }

    public void setmNID(String mNID) {
        this.mNID = mNID;
    }

    public NodeType getmType() {
        return mType;
    }

    public void setmType(NodeType mType) {
        this.mType = mType;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

	public Long getLastChangeTS() {
		return LastChangeTS;
	}

	public void setLastChangeTS(Long lastChangeTS) {
		LastChangeTS = lastChangeTS;
	}
}
