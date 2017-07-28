package community.algorithm;

class Pair<T extends Comparable<T>> implements Comparable<Pair<T>> {
	private final int row;
	private final int col;
	private final T value;

	public Pair(int row, int col, T value) {
		this.row = row;
		this.col = col;
		this.value = value;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public T getValue() {
		return value;
	}

	@Override
	public int compareTo(Pair<T> o) {
		return (this.getValue().compareTo(o.getValue()));
	}

	@Override
	public String toString() {
		return (value.toString() + "(" + row + ", " + col + ")");
	}
}