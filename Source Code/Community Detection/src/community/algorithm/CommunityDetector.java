package community.algorithm;

import java.util.ArrayList;

import community.graph.*;
import community.graphml.*;

public class CommunityDetector {
	private int nodeSize;
	private int edgeSize;
	private Matrix ajMatrix;
	private Matrix deltaQMatrix;
	private MaxHeap<Pair<Double>> maxHeap = new MaxHeap<Pair<Double>>();

	private ArrayList<Double> qVal = new ArrayList<Double>();
	private ArrayList<Double> aVec = new ArrayList<Double>();
	private ArrayList<MergeStep> steps = new ArrayList<MergeStep>();
	private ArrayList<ArrayList<Integer>> communities = new ArrayList<ArrayList<Integer>>();

	private ArrayList<Integer> idMapping1;

	private ArrayList<ArrayList<Integer>> finalCommunities = new ArrayList<ArrayList<Integer>>();

	private DeltaQFacade deltaQSelector;

	public CommunityDetector(GraphML graph, int deltaQAlg) {
		this.nodeSize = graph.getNodeSize(); // Get the number of node with
												// non-zero degree
		this.edgeSize = graph.getEdgeSize();
		this.ajMatrix = graph.getAjMatrix();
		idMapping1 = graph.getIDMapping1();

		this.deltaQMatrix = new ValuedSparseMatrix(nodeSize);

		if (deltaQAlg == 1) {
			deltaQSelector = new NormalizedDeltaQFacade(this);
		} else {
			deltaQSelector = new DeltaQFacade(this);
		}

		// Build A vector
		for (int i = 0; i < nodeSize; i++) {
			double a_i = ajMatrix.sumRow(i) / (2 * edgeSize);
			aVec.add(a_i);
		}

		// Build Delta Q Matrix
		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (ajMatrix.testValue(i, j)) {
					double deltaQ = ajMatrix.getValue(i, j) / (2 * edgeSize)
							- ajMatrix.sumRow(i) * ajMatrix.sumRow(j)
							/ Math.pow(2 * edgeSize, 2);
					deltaQMatrix.setValue(i, j, deltaQ);
				} else {
					deltaQMatrix.setValue(i, j, 0);
				}
			}

			// int maxIndexInRowI = deltaQMatrix.getMaxIndexAt(i);
			// double maxInRowI = deltaQSelector.getDeltaQ(i, maxIndexInRowI);
			// Pair<Double> pair = new Pair<Double>(i, maxIndexInRowI,
			// maxInRowI);
			// this.maxHeap.insert(pair);
		}

		updateMaxHeap();

		this.qVal.add(0.0);
	}

	/**
	 * Merge two communities, i and j. Let the merged community keep the same
	 * index with the bigger community, j. Update the delta Q matrix by: 1.
	 * Update the jth row and column of the original delta Q matrix 2. Remove
	 * the ith row and column altogether by 2.1 Copy the (size-1)th row to the
	 * ith row 2.2 Copy the (Size-1)th column to the ith column 2.3 size =
	 * size-1
	 *
	 * The input indexes i and j, i should not be larger than j. So an
	 * illegalArgument exception is thrown.
	 * 
	 * @param i
	 *            The smaller index of the community to be merged
	 * @param j
	 *            The bigger index of the community to be merged
	 */
	public void updateDeltaQMatrix(int i, int j)
			throws IllegalArgumentException {
		if (i > j) {
			throw new IllegalArgumentException(
					"updateDeltaQMatrix: The community j should have larger index than the community i.");
		}

		if (i == j) {
			throw new IllegalArgumentException(
					"updateDeltaQMatrix: The two commnities i and j to be merged are the same one --> i == j.");
		}

		int size = deltaQMatrix.getSize();
		// First update the jth row
		for (int k = 0; k < size; k++) {
			// If k is either community i or j, no update needed.
			if (k == i || k == j) {
				continue;
			} else {
				// If community k is connected to both i and j
				if (ajMatrix.testValue(k, i) && ajMatrix.testValue(k, j)) {
					double tempJK = deltaQMatrix.getValue(j, k);
					double tempIK = deltaQMatrix.getValue(i, k);
					deltaQMatrix.setValue(j, k, tempJK + tempIK);
				}
				// If community k is connected to i but not to j
				else if (ajMatrix.testValue(k, i)) {
					double tempIK = deltaQMatrix.getValue(i, k);
					deltaQMatrix.setValue(j, k,
							tempIK - 2 * aVec.get(j) * aVec.get(k));
				}
				// If community k is connected to j but not to i
				else if (ajMatrix.testValue(k, j)) {
					double tempJK = deltaQMatrix.getValue(j, k);
					deltaQMatrix.setValue(j, k,
							tempJK - 2 * aVec.get(i) * aVec.get(k));
				}
			}
		}

		// Then update the jth column from the jth row
		for (int row = 0; row < size; row++) {
			double temp = deltaQMatrix.getValue(j, row);
			deltaQMatrix.setValue(row, j, temp);
		}

		// Then copy the last row/column to the ith row/column and set the
		// deltaQMatrix size to be size-1
		deleteAtI(i, deltaQMatrix);
	}

	/**
	 * Copy the last row/column in matrix to the deleted row/column, then set
	 * the matrix size to size-1
	 * 
	 * @param deleted
	 *            The row/column to be deleted in the matrix
	 * @param matrix
	 *            The matrix to be updated
	 * @throws IllegalArgumentException
	 *             The deleted row/column cannot be larger than than the matrix
	 *             size
	 */
	public void deleteAtI(int deleted, Matrix matrix)
			throws IllegalArgumentException {
		if (deleted > matrix.getSize() - 1) {
			throw new IllegalArgumentException(
					"The deleted row/column cannot be larger than than the matrix size.");
		}
		// If the row/column to be deleted is the last row/column of the matrix,
		// set the size of matrix to be size-1
		int size = matrix.getSize();
		if (deleted + 1 == size) {
			matrix.setSize(size - 1);
		}
		// If the row/column to be deleted is not the last row/column of the
		// matrix
		else {
			// Copy the last row to the deleted row
			for (int i = 0; i < size; i++) {
				matrix.setValue(deleted, i, matrix.getValue(size - 1, i));
			}

			// Copy the last column to the deleted column
			for (int i = 0; i < size; i++) {
				matrix.setValue(i, deleted, matrix.getValue(i, size - 1));
			}

			// Set the size of the matrix to be size-1
			matrix.setSize(size - 1);
		}
	}

	// /**
	// * Update the deltaQPrime Matrix based on the updated deltaQ Matrix. This
	// * method cannot be invoked before update the deltaQ matrix during the
	// same
	// * merge
	// */
	// public void updateDeltaQPrimeMatrix() {
	// if (isUpdated) {
	// int deltaQPrimeMatrixSize = this.deltaQPrimeMatrix.getSize();
	// this.deltaQPrimeMatrix.setSize(deltaQPrimeMatrixSize - 1);
	// for (int i = 0; i < deltaQMatrix.getSize(); i++) {
	// for (int j = 0; j < deltaQMatrix.getSize(); j++) {
	// double deltaQPrime = deltaQMatrix.getValue(i, j) / aVec.get(i);
	// this.deltaQPrimeMatrix.setValue(i, j, deltaQPrime);
	// }
	// }
	// isUpdated = false;
	// } else {
	// System.out
	// .println("The deltaQ prime matrix cannot be update if the deltaQ matrix
	// has not been updated yet.");
	// }
	// }

	/**
	 * Update the adjacency matrix of the graph when two communities i and j are
	 * merged. 1. Do OR operation on ith and jth row/column and update the jth
	 * row/column 2. Copy the last row/column to the ith row/column 3. Delete
	 * the last row/column 4. Do NOT need to consider the diagonal here. Let the
	 * diagonal always be zero
	 * 
	 * @param i
	 *            The smaller index of the community to be merged
	 * @param j
	 *            The bigger index of the community to be merged
	 * 
	 * @throws IllegalArgumentException
	 *             The input indexes i and j, i should not be larger than j. So
	 *             an illegalArgument exception is thrown.
	 */
	public void updateAjMatrix(int i, int j) throws IllegalArgumentException {
		if (i > j) {
			throw new IllegalArgumentException(
					"updateDeltaQMatrix: The community j should have larger index than the community i.");
		}
		if (i == j) {
			throw new IllegalArgumentException(
					"updateDeltaQMatrix: The two commnities i and j to be merged are the same one --> i == j.");
		}

		int size = ajMatrix.getSize();
		// First do OR operation on ith and jth row and update the jth row
		for (int col = 0; col < size; col++) {
			if (ajMatrix.getValue(i, col) == 0
					&& ajMatrix.getValue(j, col) == 0) {
				ajMatrix.setValue(j, col, 0);
			} else {
				ajMatrix.setValue(j, col, 1);
			}
		}

		// Then update the jth column based on the jth row
		for (int row = 0; row < size; row++) {
			double temp = ajMatrix.getValue(j, row);
			ajMatrix.setValue(row, j, temp);
		}

		// Then set ajMatrix[j][j] = 0
		ajMatrix.setValue(j, j, 0);

		// Then copy the last row/column to the ith row/column, then set the
		// size of ajMatrix to be size-1
		this.deleteAtI(i, ajMatrix);
		// Set ajMatrix[i][i] = 0
		ajMatrix.setValue(i, i, 0);
	}

	/**
	 * Update the maxHeap based on the updated deltaQ prime matrix
	 */
	public void updateMaxHeap() {
		// Reset the Heap before any update
		maxHeap.emptyHeap();

		// Insert new elements to the heap from the updated deltaQMatrix.
		int row = 0;
		int col = deltaQMatrix.getMaxIndexAt(row);
		double value = deltaQSelector.getDeltaQ(row, col);

		for (int i = 0; i < deltaQMatrix.getSize(); i++) {
			int maxIndexInRowI = deltaQMatrix.getMaxIndexAt(i);
			double maxInRowI = deltaQSelector.getDeltaQ(i, maxIndexInRowI);
			if (maxInRowI > value) {
				row = i;
				col = maxIndexInRowI;
				value = maxInRowI;
			}
		}

		maxHeap.insert(new Pair<Double>(row, col, value));
	}

	/**
	 * Update the aVec after merge the community i and j
	 * 
	 * @param i
	 *            The smaller index of the community to be merged
	 * @param j
	 *            The bigger index of the community to be merged
	 */
	public void updateAVec(int i, int j) throws IllegalArgumentException {
		if (i > j) {
			throw new IllegalArgumentException(
					"updateAVex: The community j should have larger index than the community i.");
		}

		if (i == j) {
			throw new IllegalArgumentException(
					"updateAVex: The two commnities i and j to be merged are the same one -- i == j.");
		}
		// Set aVec'_j = aVec_j + aVec_i
		double tempJ = aVec.get(j);
		aVec.set(j, tempJ + aVec.get(i));

		// Set aVec_i be aVec_{size-1}, then remove aVec_{size-1}
		int size = aVec.size();
		aVec.set(i, aVec.get(size - 1));
		aVec.remove(size - 1);
	}

	// Update the Arraylist containing the Q value after each merge step
	// For every merge of communities i and j, we have to add both deltaQ_ij and
	// deltaQ_ji. Since deltaQ_ij = deltaQ_ji, we just times 2 for each addition
	public void updateQ() {
		Pair<Double> pair = maxHeap.getmax();
		double deltaQ = deltaQMatrix.getValue(pair.getRow(), pair.getCol());
		qVal.add(qVal.get(qVal.size() - 1) + 2.0 * deltaQ);
	}

	/**
	 * Merge two communities together, update the deltaQMatrix, aVec, and
	 * MaxHeap
	 * 
	 * @param i
	 *            The index of one community to be merged
	 * @param j
	 *            The index of the other community to be merged
	 */
	public void mergeCommunity(int i, int j) {
		if (i > j) {
			int temp = i;
			i = j;
			j = temp;
		}

		this.updateQ();
		this.updateDeltaQMatrix(i, j);
		this.updateAVec(i, j);
		this.updateAjMatrix(i, j);
		this.updateMaxHeap();
	}

	/**
	 * keep merging the node until only one community obtained
	 */
	public void mergeToOne() {
		int count = nodeSize;
		while (count > 1) {
			// System.out.println("This is the "+ count + "th merging.");
			Pair<Double> maxPair = maxHeap.getmax();
			int commI = maxPair.getRow();
			int commJ = maxPair.getCol();
			steps.add(new MergeStep(commI, commJ, deltaQMatrix.getValue(commI,
					commJ)));
			mergeCommunity(commI, commJ);
			count--;
		}
	}

	/**
	 * Find the optimal communities with the largest q value reading from the q
	 * vector
	 * 
	 * @return The q value (largest value in q vector) corresponding to the
	 *         current communities
	 */
	public double optParseCommunity() {
		// How many times of the merge has been done totally until the best
		// community detection
		int mergeTimes = 0;
		double maxQVal = qVal.get(0);
		for (int i = 1; i < this.qVal.size(); i++) {
			if (qVal.get(i) >= maxQVal) {
				maxQVal = qVal.get(i);
				mergeTimes++;
			}
		}
		// How many communities are remaining at the largest Q
		int commNum = nodeSize - mergeTimes;
		// Update the communities content
		this.updateCommunities(commNum);
		this.updateFinalCommunities();

		return maxQVal;
	}

	/**
	 * Find the communities with the required number as given
	 * 
	 * @param commNum
	 *            The generated number of communities
	 * @return the q value corresponding to the current communities
	 */
	public double parseCommunity(int commNum) {
		this.updateCommunities(commNum);
		this.updateFinalCommunities();
		double currentQVal = qVal.get(nodeSize - commNum);
		return currentQVal;
	}

	/**
	 * Update the communities with the required community number is given
	 * 
	 * @param commNum
	 *            The generated number of communities
	 * @throws IllegalArgumentException
	 *             The input of the communities number should between 1
	 *             (inclusive) and nodeSize (inclusive)
	 */
	public void updateCommunities(int commNum) throws IllegalArgumentException {
		if (commNum > nodeSize) {
			throw new IllegalArgumentException(
					"The largest number of communities must be the node size of the graph.");
		} else if (commNum < 1) {
			throw new IllegalArgumentException(
					"The smallest number of communities should be 1");
		}
		// Create n of new ArrayList, each array list only have one
		// corresponding community in this case
		for (int i = 0; i < nodeSize; i++) {
			ArrayList<Integer> commID = new ArrayList<Integer>();
			commID.add(i);
			this.communities.add(commID);
		}

		// Update the communities content based on the required community number
		// given
		for (int i = 0; i < nodeSize - commNum; i++) {
			int merged1 = steps.get(i).getI();
			int merged2 = steps.get(i).getJ();
			// Put every element in the the first to be merged community to the
			// second to be merged
			// community (communities merge)
			for (int j = 0; j < communities.get(merged1).size(); j++) {
				this.communities.get(merged2).add(
						communities.get(merged1).get(j));
			}
			// Then empty the first to be merged community
			communities.get(merged1).clear();
			// Then put the last community at this step to the first to be
			// merged
			// community (delete the first one)
			ArrayList<Integer> lastList = communities.get(nodeSize - i - 1);
			for (int k = 0; k < lastList.size(); k++) {
				communities.get(merged1).add(lastList.get(k));
			}
			// Remove the last community, the last list in the communities
			communities.remove(nodeSize - i - 1);
		}
	}

	public void updateFinalCommunities() {
		for (int i = 0; i < communities.size(); i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for (int j = 0; j < communities.get(i).size(); j++) {
				int fakeID = communities.get(i).get(j);
				int trueID = idMapping1.get(fakeID);
				temp.add(trueID);
			}
			this.finalCommunities.add(temp);
		}
	}

	public Matrix getAjMatrix() {
		return ajMatrix;
	}

	public Matrix getDeltaQMatrix() {
		return deltaQMatrix;
	}

	public ArrayList<Double> getaVec() {
		return aVec;
	}

	public ArrayList<Double> getQVal() {
		return this.qVal;
	}

	public void printQVal() {
		for (int i = 0; i < this.qVal.size(); i++) {
			System.out.println(this.qVal.get(i));
		}
	}

	public ArrayList<MergeStep> getSteps() {
		return this.steps;
	}

	public ArrayList<ArrayList<Integer>> getCommunities() {
		return communities;
	}

	public void printCommunities() {
		System.out.println("#Communities: " + this.finalCommunities.size());
		for (int i = 0; i < this.finalCommunities.size(); i++) {
			System.out.println("#Nodes in Community: "
					+ finalCommunities.get(i).size());
		}
	}

	public void printSteps() {
		if (steps.size() == 0) {
			System.out.println("No merge has been done yet!");
		} else {
			for (int i = 0; i < steps.size(); i++) {
				System.out.println(steps.get(i).toString());
			}
		}
	}
}
