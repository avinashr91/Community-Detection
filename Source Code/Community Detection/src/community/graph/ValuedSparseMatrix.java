package community.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ValuedSparseMatrix extends Matrix {
	private double defaultValue = 0.0;

	private ArrayList<Map<Integer, Double>> matrix;

	public ValuedSparseMatrix(int size) {
		super(size);
		matrix = new ArrayList<Map<Integer, Double>>();
		for (int i = 0; i < size; i++) {
			matrix.add(new HashMap<Integer, Double>());
		}
	}

	@Override
	public void setValue(int row, int col, double value) {
		if (row > size - 1 || col > size - 1) {
			throw new IllegalArgumentException(
					"Set value in the matrix: row or column should not be out of the matrix size");
		}

		Map<Integer, Double> rowMap = matrix.get(row);
		if (value == defaultValue) {
			rowMap.remove(col);
		} else {
			rowMap.put(col, value);
		}
	}

	@Override
	public double getValue(int row, int col) {
		if (row > size - 1 || col > size - 1) {
			throw new IllegalArgumentException(
					"Set value in the matrix: row or column should not be out of the matrix size");
		}

		Map<Integer, Double> rowMap = matrix.get(row);
		return rowMap.containsKey(col) ? rowMap.get(col) : defaultValue;
	}

	@Override
	public boolean testValue(int row, int col) {
		if (row > size - 1 || col > size - 1) {
			throw new IllegalArgumentException(
					"Set value in the matrix: row or column should not be out of the matrix size");
		}

		return matrix.get(row).containsKey(col);
	}

	@Override
	public void printMatrix() {
		for (int i = 0; i < size; i++) {
			Map<Integer, Double> rowMap = matrix.get(i);
			for (int j = 0; j < size; j++) {
				System.out.print(String.format("%7.5f",
						(rowMap.containsKey(j) ? rowMap.get(j) : defaultValue))
						+ "  ");
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

		Map<Integer, Double> rowMap = matrix.get(row);
		double sum = 0;
		for (Integer key : rowMap.keySet()) {
			if (key < size) {
				sum += rowMap.get(key);
			}
		}
		return sum;
	}

	@Override
	public int getMaxIndexAt(int row) {
		if (row > size - 1) {
			throw new IllegalArgumentException(
					"Sum row in the matrix: row should not be out of the matrix size");
		}

		Map<Integer, Double> rowMap = matrix.get(row);
		int idxOfMax = -1;
		double maxVal = -Double.MAX_VALUE;
		for (Integer key : rowMap.keySet()) {
			if (key < size && key != row && rowMap.get(key) > maxVal) {
				maxVal = rowMap.get(key);
				idxOfMax = key;
			}
		}

		if (defaultValue > maxVal) {
//			System.out.println("Alert");
			for (int i = 0; i < size; i++) {
				if (i != row && !rowMap.containsKey(i)) {
					maxVal = defaultValue;
					idxOfMax = i;
				}
			}
		}

		return idxOfMax;
	}
}
