package community.algorithm;

/** Source code example for "A Practical Introduction to Data
Structures and Algorithm Analysis, 3rd Edition (Java)" 
by Clifford A. Shaffer
Copyright 2008-2011 by Clifford A. Shaffer
*/

import java.lang.Comparable;
import java.util.ArrayList;

/** Max-heap implementation */
public class MaxHeap<E extends Comparable<? super E>> {
	private ArrayList<E> Heap; // Pointer to the heap array
	// private int size; // Maximum size of the heap
	// private int n; // Number of things in heap

	public MaxHeap() {
		Heap = new ArrayList<E>();
	}

	// /** Constructor supporting preloading of heap contents */
	// public MaxHeap(ArrayList<E> h, int num, int max) {
	// Heap = h;
	// n = num;
	// size = max;
	// buildheap();
	// }

	/** @return Current size of the heap */
	public int heapsize() {
		return Heap.size();
	}

	/** @return True if pos a leaf position, false otherwise */
	public boolean isLeaf(int pos) {
		return (pos >= Heap.size() / 2) && (pos < Heap.size());
	}

	/** @return Position for left child of pos */
	public int leftchild(int pos) {
		assert pos < Heap.size() / 2 : "Position has no left child";
		return 2 * pos + 1;
	}

	/** @return Position for right child of pos */
	public int rightchild(int pos) {
		assert pos < (Heap.size() - 1) / 2 : "Position has no right child";
		return 2 * pos + 2;
	}

	/** @return Position for parent */
	public int parent(int pos) {
		assert pos > 0 : "Position has no parent";
		return (pos - 1) / 2;
	}

	private void swap(ArrayList<E> heap, int fpos, int spos) {
		E tmp = heap.get(fpos);
		heap.set(fpos, heap.get(spos));
		heap.set(spos, tmp);
	}

	/** Insert val into heap */
	public void insert(E val) {

		int curr = Heap.size();
		Heap.add(val);

		Heap.set(curr, val); // Start at end of heap
		// Now sift up until curr's parent's key > curr's key
		while ((curr != 0) && (Heap.get(curr).compareTo(Heap.get(parent(curr))) > 0)) {
			swap(Heap, curr, parent(curr));
			curr = parent(curr);
		}
	}

	/** Heapify contents of Heap */
	public void buildheap() {
		for (int i = Heap.size() / 2 - 1; i >= 0; i--)
			siftdown(i);
	}

	/** Put element in its correct place */
	private void siftdown(int pos) {
		assert (pos >= 0) && (pos < Heap.size()) : "Illegal heap position";
		while (!isLeaf(pos)) {
			int j = leftchild(pos);
			if ((j < (Heap.size() - 1)) && (Heap.get(j).compareTo(Heap.get(j + 1)) < 0))
				j++; // j is now index of child with greater value
			if (Heap.get(pos).compareTo(Heap.get(j)) >= 0)
				return;
			swap(Heap, pos, j);
			pos = j; // Move down
		}
	}

	/** Remove and return maximum value */
	public E removemax() {
		assert Heap.size() > 0 : "Removing from empty heap";
		swap(Heap, 0, Heap.size() - 1); // Swap maximum with last value
		E maxVal = Heap.remove(Heap.size() - 1);
		if (Heap.size() != 0) // Not on last element
			siftdown(0); // Put new heap root val in correct place

		return maxVal;
	}

	/** Return the maximum value but not remove it */
	public E getmax() {
		assert Heap.size() > 0 : "Get maximum value from empty heap";
		return Heap.get(0);
	}

	/** Remove and return element at specified position */
	public E remove(int pos) {
		assert (pos >= 0) && (pos < Heap.size()) : "Illegal heap position";
		E removed;
		if (pos == (Heap.size() - 1)) {
			removed = Heap.remove(Heap.size() - 1);
		} // Last element, no work to be done
		else {
			swap(Heap, pos, Heap.size() - 1); // Swap with last value
			removed = Heap.remove(Heap.size() - 1);
			// If we just swapped in a big value, push it up
			while ((pos > 0) && (Heap.get(pos).compareTo(Heap.get(parent(pos))) > 0)) {
				swap(Heap, pos, parent(pos));
				pos = parent(pos);
			}
			if (Heap.size() != 0) {
				siftdown(pos);
			} // If it is little, push down
		}
		return removed;
	}

	/**
	 * Print the heap, each fine elements per line 
	 */
	// public void printHeap() {
	// for (int i = 0; i < Heap.size(); i++) {
	// System.out.print(Heap.get(i).toString() + " ");
	// if (i % 5 == 4) {
	// System.out.println();
	// }
	// }
	// System.out.println();
	// }

	/**
	 * Print the heap, each element has one separate line 
	 */
	public void printHeap() {
		for (int i = 0; i < Heap.size(); i++) {
			System.out.println(Heap.get(i).toString() + " ");
			// if (i % 5 == 4) {
			// System.out.println();
			// }
			// }
			// System.out.println();
		}
	}

	public void emptyHeap() {
		Heap.clear();
	}
}