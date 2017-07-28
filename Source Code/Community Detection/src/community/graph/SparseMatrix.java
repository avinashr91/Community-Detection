package community.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SparseMatrix extends Matrix {
	private ArrayList<Set<Integer>> matrix;

	public SparseMatrix(int size) {
		super(size);

		matrix = new ArrayList<Set<Integer>>();
		for (int i = 0; i < size; i++) {
			matrix.add(new HashSet<Integer>());
		}
	}

	@Override
	public void setValue(int row, int col, double value) {
		if (row > size - 1 || col > size - 1) {
			throw new IllegalArgumentException(
					"Set value in the matrix: row or column should not be out of the matrix size");
		}

		Set<Integer> rowSet = matrix.get(row);
		if (value == 0.0) {
			rowSet.remove(col);
		} else if (value == 1.0) {
			rowSet.add(col);
		} else {
			throw new IllegalArgumentException(
					"Sparse matrix does not support values except 0 and 1");
		}
	}

	@Override
	public double getValue(int row, int col) {
		return testValue(row, col) ? 1.0 : 0.0;
	}

	@Override
	public boolean testValue(int row, int col) {
		if (row > size - 1 || col > size - 1) {
			throw new IllegalArgumentException(
					"Get value from the matrix: row or column should not be out of the matrix size");
		}
		return matrix.get(row).contains(col);
	}

	@Override
	public void printMatrix() {
		for (int i = 0; i < size; i++) {
			ArrayList<Integer> rowList = new ArrayList<Integer>(matrix.get(i));
			Collections.sort(rowList);
			int idx = 0;
			for (int j = 0; j < size; j++) {
				if (j == rowList.get(idx)) {
					System.out.print(String.format("%7.5f", 1.0) + "  ");
					idx++;
				} else {
					System.out.print(String.format("%7.5f", 0.0) + "  ");
				}
			}
			System.out.println();
		}
	}

	@Override
	public double sumRow(int row) {
		if (row > size - 1) {
			throw new IllegalArgumentException(
					"Sum row in the matrix: row should not be out of the matrix size");
		}

		Set<Integer> rowSet = matrix.get(row);
		double sum = 0;
		for (Integer idx : rowSet) {
			if (idx < size) {
				sum++;
			}
		}
		return sum;
	}

	@Override
	public int getMaxIndexAt(int row) {
		if (row > size - 1) {
			throw new IllegalArgumentException(
					"Get the index of the max value in the row: row should not be out of the matrix size");
		}

		Set<Integer> rowSet = matrix.get(row);
		for (Integer idx : rowSet) {
			if (idx < size) {
				return idx;
			}
		}
		return 0;
	}
}
