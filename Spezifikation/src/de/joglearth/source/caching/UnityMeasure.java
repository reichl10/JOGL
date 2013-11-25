package de.joglearth.source.caching;

public class UnityMeasure<T> implements ObjectMeasure<T> {

	@Override
	public int getSize(T t) {
		return 1;
	}

}
