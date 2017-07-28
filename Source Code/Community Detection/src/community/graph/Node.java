package community.graph;

public class Node {
	private final int mID;
	private String mValue;

	public Node(int x) {
		mID = x;
	}

	public int getID() {
		return mID;
	}

	public void setValue(String value) {
		this.mValue = value;
	}

	public String getValue() {
		return (mValue);
	}

	public String toString() {
		return (this.getValue());
	}
}
