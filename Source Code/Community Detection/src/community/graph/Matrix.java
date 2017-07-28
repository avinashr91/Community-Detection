package community.graph;

public abstract class Matrix {
	protected int size;

	public Matrix(int size) {
		this.size = size;
	}

	abstract public void setValue(int row, int col, double value);

	abstract public double getValue(int row, int col);

	public void setSize(int size) {
		if (size > this.size) {
			throw new IllegalArgumentException("Unable to extend the matrix");
		}
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	// abstract public double[] getRow(int row);
	abstract public boolean testValue(int row, int col);

	abstract public void printMatrix();

	abstract public double sumRow(int row);

	/**
	 * Get the index of the largest value at the given row. Ignore the value in
	 * the diagonal. If the max value occurs at the diagonal line, ignore it and
	 * return the second largest value's index
	 * 
	 * @param row
	 * @return The index of the largest value at the given row if the largest
	 *         value is not in the diagonal, or the index of the second largest
	 *         value if the max value is in the diagonal
	 */
	abstract public int getMaxIndexAt(int row);
}
