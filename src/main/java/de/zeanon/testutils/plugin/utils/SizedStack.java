package de.zeanon.testutils.plugin.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SizedStack<T> {

	private final int maxSize;
	private final Object[] data;
	@Getter
	private int size;
	private int head;
	private int tail;

	public SizedStack(int size) {
		this.maxSize = size;
		this.data = new Object[this.maxSize];
		this.head = 0;
		this.tail = 0;
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

	/**
	 * Shifts the contents of this SizedStack to a new one with the given size.
	 * This SizedStack is depleted in the process.
	 *
	 * @param newSize the size the new SizedStack should have
	 *
	 * @return a new SizedStack with the given size
	 */
	public SizedStack<T> resize(final int newSize) {
		final @NotNull SizedStack<T> temp = new SizedStack<>(newSize);
		while (!this.isEmpty() && temp.size < newSize) {
			temp.pushBottom(this.pop());
		}
		return temp;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isEmpty() {
		return this.size == 0;
	}

	private void pushBottom(final @Nullable T element) {
		this.decreaseTail();
		this.data[this.tail] = element;
		this.increaseSize();
	}

	private void increaseHead() {
		this.head++;
		while (this.head > this.maxSize - 1) {
			this.head -= this.maxSize;
		}
		if (this.head == this.tail + 1) {
			this.increaseTail();
		}
	}

	private void decreaseHead() {
		this.head--;
		while (this.head < 0) {
			this.head += this.maxSize;
		}
		if (this.head == this.tail - 1) {
			this.decreaseTail();
		}
	}

	private void increaseTail() {
		this.tail++;
		while (this.tail > this.maxSize - 1) {
			this.tail -= this.maxSize;
		}
		if (this.tail == this.head + 1) {
			this.increaseHead();
		}
	}

	private void decreaseTail() {
		this.tail--;
		while (this.tail < 0) {
			this.tail += this.maxSize;
		}
		if (this.tail == this.head - 1) {
			this.decreaseHead();
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