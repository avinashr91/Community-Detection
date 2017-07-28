package community.algorithm;

public class MergeStep {

	private int i;
	private int j;
	private double deltaQ;

	public MergeStep(int i, int j, double deltaQ) {
		this.i = i;
		this.j = j;
		this.deltaQ = deltaQ;
	}

	public int getI() {
		return i;
	}

	// public void setI(int i) {
	// this.i = i;
	// }
	public int getJ() {
		return j;
	}

	// public void setJ(int j) {
	// this.j = j;
	// }
	public double getDeltaQ() {
		return deltaQ;
	}

	// public void setDeltaQ(double deltaQ) {
	// this.deltaQ = deltaQ;
	// }

	@Override
	public String toString() {
		return ("Merge Community " + i + ", " + j + " (deltaQ = "
				+ String.format("%.3f", this.deltaQ) + ")");
	}

}
