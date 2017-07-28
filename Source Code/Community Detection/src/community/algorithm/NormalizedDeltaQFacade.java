package community.algorithm;

public class NormalizedDeltaQFacade extends DeltaQFacade {
	public NormalizedDeltaQFacade(CommunityDetector det) {
		super(det);
	}

	@Override
	public double getDeltaQ(int i, int j) {
		return det.getDeltaQMatrix().getValue(i, j) / det.getaVec().get(i);
	}
}
