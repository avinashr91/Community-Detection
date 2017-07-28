package community.graph;

public class FullMatrix extends Matrix {
	private double[][] matrix;

	public FullMatrix(int size) {
		super(size);
		matrix = new double[size][size];
	}

	@Override
	public void setValue(int row, int col, double value)
			throws IllegalArgumentException {
		if (row > size - 1 || col > size - 1) {
			throw new IllegalArgumentException(
					"Set value in the matrix: row or column should not be out of the matrix size");
		}
		matrix[row][col] = value;
	}

	@Override
	public double getValue(int row, int col) throws IllegalArgumentException {
		if (row > size - 1 || col > size - 1) {
			throw new IllegalArgumentException(
					"Get value from the matrix: row or column should not be out of the matrix size");
		}
		return matrix[row][col];
	}

	@Override
	public boolean testValue(int row, int col) throws IllegalArgumentException {
		if (row > size - 1 || col > size - 1) {
			throw new IllegalArgumentException(
					"Test value in the matrix: row or column should not be out of the matrix size");
		}
		if (matrix[row][col] == 0)
			return false;
		else
			return true;
	}

	public void printMatrix() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(String.format("%7.5f", matrix[i][j]) + "  ");
			}
			System.out.println();
		}
	}

	@Override
	public double sumRow(int row) throws IllegalArgumentException {
		if (row > size - 1) {
			throw new IllegalArgumentException(
					"Sum row in the matrix: row should not be out of the matrix size");
		}
		double sum = 0;
		for (int j = 0; j < size; j++) {
			sum += matrix[row][j];
		}

		return sum;
	}

	@Override
	public int getMaxIndexAt(int row) throws IllegalArgumentException {
		if (row > size - 1) {
			throw new IllegalArgumentException(
					"Get the index of the max value in the row: row should not be out of the matrix size");
		}

		int idxOfMaxVal = -1;
		double maxVal = -Double.MAX_VALUE;

		if (size == 1) { // Only one element in the matrix
			idxOfMaxVal = 0;
		} else {
			for (int i = 0; i < size; i++) {
				if (i != row) { // Do not consider the value in the diagonal
					if (matrix[row][i] > maxVal) {
						maxVal = matrix[row][i];
						idxOfMaxVal = i;
					}
				}
			}
		}

		return idxOfMaxVal;
	}

	// public double[] getRow(int row) throws IllegalArgumentException{
	//
	// if (row > size - 1 ) {
	// throw new IllegalArgumentException("Get each row of the matrix: row
	// should not be out of the matrix size");
	// }
	// return matrix[row];
	// }
}
