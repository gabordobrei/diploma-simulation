package hu.dobrei.diploma;

import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class Stat {
	Table<Integer, Integer, Integer> scatter = HashBasedTable.create();
	/*
	boolean zoom = true;
	/*/
	boolean zoom = false;
	//*/
	int step;
	int size = 0;
	int max = 0;

	public Stat(List<Ent> list) {
		size = list.size();

		step = zoom ? 1 : 10;

		for (Ent e : list) {
			max = Math.max(max, Math.max(e.out, e.in));
		}

		for (int i = 0; i < max / step + 1; i++) {
			for (int j = 0; j < max / step + 1; j++) {
				scatter.put(i * step, j * step, 0);
			}
		}

		Range<Integer> outRange;
		Range<Integer> inRange;
		for (Ent e : list) {
			int out = e.out;
			int in = e.in;

			for (Cell<Integer, Integer, Integer> c : scatter.cellSet()) {
				outRange = Range.openClosed(c.getRowKey(), c.getRowKey() + step);
				inRange = Range.openClosed(c.getColumnKey(), c.getColumnKey() + step);
				if (outRange.contains(out) && inRange.contains(in)) {
					int count = c.getValue();
					scatter.put(c.getRowKey(), c.getColumnKey(), ++count);
				}
			}
		}

		if (zoom) {
			printZoom();
		} else {
			print();
		}
	}

	private void print() {
		for (Cell<Integer, Integer, Integer> c : scatter.cellSet()) {
			if (c.getValue() != 0) {
				double val = (double) (c.getValue() / (double) size);
				int in = c.getColumnKey();
				int out = c.getRowKey();
				System.out.println(in + " " + out + " " + val);
			}
		}
	}

	private void printZoom() {
		for (Cell<Integer, Integer, Integer> c : scatter.cellSet()) {
			if (c.getValue() != 0 && c.getRowKey() < 31 && c.getColumnKey() < 31) {
				double val = (double) (c.getValue() / (double) size);
				int in = c.getColumnKey();
				int out = c.getRowKey();
				System.out.println(in + " " + out + " " + val);
			}
		}
	}
}

class Ent {
	public int out, in;

	public Ent(int o, int i) {
		out = o;
		in = i;
	}
}