package it.univaq.disim.memorec.memorec.jjodel.buisiness;

import java.util.Comparator;
import java.util.Map;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;

public class ValueComparator implements Comparator<Model> {
	Map<Model, Float> base;

	public ValueComparator(Map<Model, Float> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.
	public int compare(Model a, Model b) {
		float va = base.get(a);
		float vb = base.get(b);
		if (va > vb) {
			return -1;
		} else if (va == vb) {
			return a.getName().compareTo(b.getName());
		} else {
			return 1;
		}
	}
}