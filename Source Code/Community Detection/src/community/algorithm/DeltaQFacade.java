package community.algorithm;

public class DeltaQFacade {
	protected CommunityDetector det;

	public DeltaQFacade(CommunityDetector det) {
		this.det = det;
	}

	public double getDeltaQ(int i, int j) {
		return det.getDeltaQMatrix().getValue(i, j);
	}
}
