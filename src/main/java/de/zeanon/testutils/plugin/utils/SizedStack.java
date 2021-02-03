package de.zeanon.testutils.plugin.utils;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;


public class SizedStack<T> {

	private final int maxSize;
	private final Object[] data;
	@Getter
	private int size;
	private int head;

	public SizedStack(int size) {
		this.maxSize = size;
		this.data = new Object[this.maxSize];
		this.head = 0;
		this.size = 0;
	}

	public void push(final @Nullable T element) {
		this.data[this.head] = element;
		this.increaseHead();
		this.increaseSize();
	}

	public T pop() {
		this.decreaseHead();
		this.decreaseSize();
		//noinspection unchecked
		final T result = (T) this.data[this.head];
		this.data[this.head] = null;
		return result;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	private void increaseHead() {
		this.head++;
		while (this.head > this.maxSize - 1) {
			this.head -= this.maxSize;
		}
	}

	private void decreaseHead() {
		this.head--;
		while (this.head < 0) {
			this.head += this.maxSize;
		}
	}

	private void increaseSize() {
		if (this.size < this.maxSize) {
			this.size++;
		}
	}

	private void decreaseSize() {
		if (this.size > 0) {
			this.size--;
		}
	}
}