/*
 * Copyright 2012 by Thomas Mauch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id: GapList.java 4092 2018-07-20 00:38:43Z origo $
 *
 * Copied by Zeanon to reduce final jar size
 */
package de.zeanon.testutils.browniescollections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;


/**
 * GapList combines the strengths of both ArrayList and LinkedList.
 * It is implemented to offer both efficient random access to elements
 * by index (as ArrayList does) and at the same time efficient adding
 * and removing elements to and from beginning and end (as LinkedList does).
 * It also exploits the locality of reference often seen in applications
 * to further improve performance, e.g. for iterating over the list.
 *
 * <strong>Note that this implementation is not synchronized.</strong>
 * <p>
 * Note that the iterators provided are not fail-fast.
 *
 * @param <E> type of elements stored in the list
 *
 * @author Thomas Mauch
 * @version $Id: GapList.java 4092 2018-07-20 00:38:43Z origo $
 * @see java.util.List
 * @see java.util.ArrayList
 * @see java.util.LinkedList
 */
public class GapList<E> extends IList<E> {

	/*
	 * Helper variables to enable code for debugging.
	 * As the variables are declared as "static final boolean", the JVM
	 * will be able to detect unused branches and will not execute the
	 * code (the same approach is used for the assert statement).
	 */
	/**
	 * Default capacity for list
	 */
	public static final int DEFAULT_CAPACITY = 10;
	/**
	 * If true the invariants the GapList are checked for debugging
	 */
	private static final boolean DEBUG_CHECK = false;
	/**
	 * If true the calls to some methods are traced out for debugging
	 */
	private static final boolean DEBUG_TRACE = false;
	/**
	 * If true the internal state of the GapList is traced out for debugging
	 */
	private static final boolean DEBUG_DUMP = false;

	// -- EMPTY --

	// Cannot make a static reference to the non-static type E:
	// public static GapList<E> EMPTY = GapList.create().unmodifiableList();
	// Syntax error:
	// public static <EE> GapList<EE> EMPTY = GapList.create().unmodifiableList();
	/**
	 * Unmodifiable empty instance
	 */
	@SuppressWarnings("rawtypes")
	private static final GapList EMPTY = GapList.create().unmodifiableList();
	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -4477005565661968383L;
	/**
	 * Empty array used for default initialization
	 */
	private static final Object[] EMPTY_VALUES = new Object[0];
	/**
	 * Array holding raw data
	 */
	private E[] values;
	/**
	 * Number of elements stored in this GapList
	 */
	private int size;
	/**
	 * Physical position of first element
	 */
	private int start;
	/**
	 * Physical position after last element
	 */
	private int end;
	/**
	 * Size of gap (0 if there is no gap)
	 */
	private int gapSize;
	/**
	 * Logical index of first element after gap (ignored if gapSize=0)
	 */
	private int gapIndex;
	/**
	 * Physical position of first slot in gap (ignored if gapSize=0)
	 */
	private int gapStart;

	/**
	 * Construct a list with the default initial capacity.
	 */
	public GapList() {
		this.init();
	}


	// --- Static methods ---

	/**
	 * Construct a list with specified initial capacity.
	 *
	 * @param capacity capacity
	 */
	public GapList(int capacity) {
		this.init(new Object[capacity], 0);
	}

	/**
	 * Construct a list to contain the specified elements.
	 * The list will have an initial capacity to hold these elements.
	 *
	 * @param coll collection with elements
	 */
	public GapList(Collection<? extends E> coll) {
		this.init(coll);
	}

	/**
	 * Constructor used internally, e.g. for ImmutableGapList.
	 *
	 * @param copy true to copy all instance values from source,
	 *             if false nothing is done
	 * @param that list to copy
	 */
	protected GapList(boolean copy, GapList<E> that) {
		if (copy) {
			this.doAssign(that);
		}
	}

	/**
	 * @return unmodifiable empty instance
	 */
	@SuppressWarnings("unchecked")
	public static <EE> GapList<EE> EMPTY() {
		return GapList.EMPTY;
	}

	/**
	 * Create new list.
	 *
	 * @param <E> type of elements stored in the list
	 *
	 * @return created list
	 */
	// This separate method is needed as the varargs variant creates the list with specific size
	public static <E> GapList<E> create() {
		return new GapList<>();
	}

	/**
	 * Create new list with specified elements.
	 *
	 * @param coll collection with element
	 * @param <E>  type of elements stored in the list
	 *
	 * @return created list
	 */
	public static <E> GapList<E> create(Collection<? extends E> coll) {
		return new GapList<>((coll != null) ? coll : Collections.emptyList());
	}

	/**
	 * Create new list with specified elements.
	 *
	 * @param elems array with elements
	 * @param <E>   type of elements stored in the list
	 *
	 * @return created list
	 */
	public static <E> GapList<E> create(E... elems) {
		GapList<E> list = new GapList<>();
		if (elems != null) {
			if (elems != null) {
				list.init(elems);
			}
		}
		return list;
	}

	@Override
	public E getDefaultElem() {
		return null;
	}

	/**
	 * Returns a shallow copy of this <tt>GapList</tt> instance.
	 * (the new list will contain the same elements as the source list, i.e. the elements themselves are not copied).
	 * This method is identical to clone() except that the result is casted to GapList.
	 *
	 * @return a copy of this <tt>GapList</tt> instance
	 */
	@Override
	public GapList<E> copy() {
		return (GapList<E>) super.copy();
	}

	/**
	 * Increases the capacity of this <tt>GapList</tt> instance, if
	 * necessary, to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 *
	 * @param minCapacity the desired minimum capacity
	 */
	// Only overridden to change Javadoc
	@Override
	public void ensureCapacity(int minCapacity) {
		super.ensureCapacity(minCapacity);
	}

	/**
	 * Returns a shallow copy of this <tt>GapList</tt> instance
	 * (The elements themselves are not copied).
	 * The capacity of the list will be set to the number of elements,
	 * so after calling clone(), size and capacity are equal.
	 *
	 * @return a copy of this <tt>GapList</tt> instance
	 */
	// Only overridden to change Javadoc
	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public GapList<E> unmodifiableList() {
		// Naming as in java.util.Collections#unmodifiableList
		return new ImmutableGapList<>(this);
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public int capacity() {
		return this.values.length;
	}

	@Override
	public E get(int index) {
		// A note about the inlining capabilities of the Java HotSpot Performance Engine
		// (http://java.sun.com/developer/technicalArticles/Networking/HotSpot/inlining.html)
		// The JVM seems not able to inline the methods called within
		// this method, irrespective whether they are "private final" or not.
		// Also -XX:+AggressiveOpts seems not to help.
		// We therefore do inlining manually.

		// INLINE: checkIndex(index);
		if (index < 0 || index >= this.size()) {
			throw new IndexOutOfBoundsException("Invalid index: " + index + " (size: " + this.size() + ")");
		}
		return this.doGet(index);
	}

	@Override
	public boolean add(E elem) {
		if (GapList.DEBUG_TRACE) {
			this.debugLog("add: " + elem);
			if (GapList.DEBUG_DUMP) {
				this.debugDump();
			}
		}
		return this.doAdd(-1, elem);
	}

	@Override
	public void add(int index, E elem) {
		if (GapList.DEBUG_TRACE) {
			this.debugLog("add: " + index + ", " + elem);
			if (GapList.DEBUG_DUMP) {
				this.debugDump();
			}
		}
		this.checkIndexAdd(index);
		this.doAdd(index, elem);
	}

	@Override
	public GapList<E> getAll(int index, int len) {
		this.checkRange(index, len);

		GapList<E> list = this.doCreate(len);
		list.size = len;
		this.doGetAll(list.values, index, len);
		return list;
	}

	@Override
	public GapList<E> getAll(E elem) {
		return (GapList<E>) super.getAll(elem);
	}

	@Override
	public <R> GapList<R> mappedList(Function<E, R> mapper) {
		return (GapList<R>) super.mappedList(mapper);
	}

	@Override
	public E remove(int index) {
		this.checkIndex(index);

		if (GapList.DEBUG_TRACE) {
			this.debugLog("remove: " + index);
			if (GapList.DEBUG_DUMP) {
				this.debugDump();
			}
		}
		return this.doRemove(index);
	}

	/**
	 * Trims the capacity of this GapList instance to be the list's current size.
	 * An application can use this operation to minimize the storage of an instance.
	 */
	@Override
	public void trimToSize() {
		this.doModify();

		if (this.size < this.values.length) {
			this.init(this.toArray(), this.size);
		}
	}

	@Override
	public GapList<E> doCreate(int capacity) {
		if (capacity == -1) {
			capacity = GapList.DEFAULT_CAPACITY;
		}
		return new GapList<>(capacity);
	}

	@Override
	public void sort(int index, int len, Comparator<? super E> comparator) {
		this.checkRange(index, len);

		this.normalize();
		Arrays.sort(this.values, index, index + len, comparator);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> int binarySearch(int index, int len, K key, Comparator<? super K> comparator) {
		this.checkRange(index, len);

		this.normalize();
		return Arrays.binarySearch(this.values, index, index + len, key, (Comparator<Object>) comparator);
	}

	/**
	 * Initialize the list to be empty.
	 * The list will have the default initial capacity.
	 */
	void init() {
		this.init(GapList.EMPTY_VALUES, 0);
	}

	/**
	 * Initialize the list to contain the specified elements only.
	 * The list will have an initial capacity to hold these elements.
	 *
	 * @param coll collection with elements
	 */
	void init(Collection<? extends E> coll) {
		Object[] array = IList.toArray(coll);
		this.init(array, array.length);
	}

	/**
	 * Initialize the list to contain the specified elements only.
	 * The list will have an initial capacity to hold these elements.
	 *
	 * @param elems array with elements
	 */
	void init(E... elems) {
		Object[] array = elems.clone();
		this.init(array, array.length);
	}

	/**
	 * Initialize all instance fields.
	 *
	 * @param values new values array
	 * @param size   new size
	 */
	@SuppressWarnings("unchecked")
	void init(Object[] values, int size) {
		this.values = (E[]) values;
		this.size = size;

		// start and end are both 0 because either size == 0 or size == values.length
		this.start = 0;
		this.end = 0;
		this.gapSize = 0;
		this.gapStart = 0;
		this.gapIndex = 0;

		if (GapList.DEBUG_CHECK) {
			this.debugCheck();
		}
	}

	@Override
	protected void doAssign(IList<E> that) {
		GapList<E> list = (GapList<E>) that;
		this.values = list.values;
		this.size = list.size;
		this.start = list.start;
		this.end = list.end;
		this.gapSize = list.gapSize;
		this.gapIndex = list.gapIndex;
		this.gapStart = list.gapStart;
	}

	@Override
	protected void doClone(IList<E> that) {
		// Do not simply clone the array, but make sure its capacity
		// is equal to the size (as in ArrayList)
		this.init(that.toArray(), that.size());
	}

	@Override
	protected void doClear() {
		this.init(this.values, 0);
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = null;
		}
	}

	@Override
	protected E doGet(int index) {
		assert (index >= 0 && index < this.size);

		// INLINE: return values[physIndex(index)];
		int physIdx = index + this.start;
		if (index >= this.gapIndex) {
			physIdx += this.gapSize;
		}
		if (physIdx >= this.values.length) {
			physIdx -= this.values.length;
		}
		return this.values[physIdx];
	}

	@Override
	protected E doSet(int index, E elem) {
		assert (index >= 0 && index < this.size);

		int physIdx = this.physIndex(index);
		E oldElem = this.values[physIdx];
		this.values[physIdx] = elem;
		return oldElem;
	}

	@Override
	protected E doReSet(int index, E elem) {
		assert (index >= 0 && index < this.size);

		int physIdx = this.physIndex(index);
		E oldElem = this.values[physIdx];
		this.values[physIdx] = elem;
		return oldElem;
	}

	@Override
	protected boolean doAdd(int index, E elem) {
		this.doEnsureCapacity(this.size + 1);

		if (index == -1) {
			index = this.size;
		}
		assert (index >= 0 && index <= this.size);

		int physIdx;
		// Add at last position
		if (index == this.size && (this.end != this.start || this.size == 0)) {
			if (GapList.DEBUG_TRACE) {
				this.debugLog("Case A0");
			}
			physIdx = this.end;
			this.end++;
			if (this.end >= this.values.length) {
				this.end -= this.values.length;
			}

			// Add at first position
		} else if (index == 0 && (this.end != this.start || this.size == 0)) {
			if (GapList.DEBUG_TRACE) {
				this.debugLog("Case A1");
			}
			this.start--;
			if (this.start < 0) {
				this.start += this.values.length;
			}
			physIdx = this.start;
			if (this.gapSize > 0) {
				this.gapIndex++;
			}

			// Shrink gap
		} else if (this.gapSize > 0 && index == this.gapIndex) {
			if (GapList.DEBUG_TRACE) {
				this.debugLog("Case A2");
			}
			physIdx = this.gapStart + this.gapSize - 1;
			if (physIdx >= this.values.length) {
				physIdx -= this.values.length;
			}
			this.gapSize--;

			// Add at other positions
		} else {
			physIdx = this.physIndex(index);

			if (this.gapSize == 0) {
				// Create new gap
				if (this.start < this.end && this.start > 0) {
					// S4: Space is at head and tail
					assert (this.debugState() == 4);
					int len1 = physIdx - this.start;
					int len2 = this.end - physIdx;
					if (len1 <= len2) {
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case A3");
						}
						this.moveData(this.start, 0, len1);
						this.gapSize = this.start - 1;
						this.gapStart = len1;
						this.gapIndex = len1;
						this.start = 0;
						physIdx--;
					} else {
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case A4");
						}
						this.moveData(physIdx, this.values.length - len2, len2);
						this.gapSize = this.values.length - this.end - 1;
						this.gapStart = physIdx + 1;
						this.gapIndex = index + 1;
						this.end = 0;
					}
				} else if (physIdx < this.end) {
					assert (this.debugState() == 2 || this.debugState() == 5);
					if (GapList.DEBUG_TRACE) {
						this.debugLog("Case A5");
					}
					int len = this.end - physIdx;
					int rightSize = (this.start - this.end + this.values.length) % this.values.length;
					this.moveData(physIdx, this.end + rightSize - len, len);
					this.end = this.start;
					this.gapSize = rightSize - 1;
					this.gapStart = physIdx + 1;
					this.gapIndex = index + 1;
				} else {
					assert (this.debugState() == 3 || this.debugState() == 5);
					assert (physIdx > this.end);
					if (GapList.DEBUG_TRACE) {
						this.debugLog("Case A6");
					}
					int len = physIdx - this.start;
					int rightSize = this.start - this.end;
					this.moveData(this.start, this.end, len);
					this.start -= rightSize;
					this.end = this.start;
					this.gapSize = rightSize - 1;
					this.gapStart = this.start + len;
					this.gapIndex = index;
					physIdx--;
				}
			} else {
				// Move existing gap
				boolean moveLeft;
				int gapEnd = (this.gapStart + this.gapSize - 1) % this.values.length + 1;
				if (gapEnd < this.gapStart) {
					assert (this.debugState() == 9 || this.debugState() == 12);
					// Gap is at head and tail
					int len1 = physIdx - gapEnd;
					int len2 = this.gapStart - physIdx - 1;
					if (len1 <= len2) {
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case A7a");
						}
						moveLeft = true;
					} else {
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case A8a");
						}
						moveLeft = false;
					}
				} else {
					assert (this.debugState() == 6 || this.debugState() == 7 || this.debugState() == 8 || this.debugState() == 9 || this.debugState() == 10 ||
							this.debugState() == 11 || this.debugState() == 12 || this.debugState() == 13 || this.debugState() == 14 || this.debugState() == 15);
					if (physIdx > this.gapStart) {
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case A7b");
						}
						moveLeft = true;
					} else {
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case A8b");
						}
						moveLeft = false;
					}
				}
				if (moveLeft) {
					int src = this.gapStart + this.gapSize;
					int dst = this.gapStart;
					int len = physIdx - gapEnd;
					this.moveDataWithGap(src, dst, len);
					physIdx--;
					this.gapSize--;
					this.gapIndex = index;
					this.gapStart += len;
					if (this.gapStart >= this.values.length) {
						this.gapStart -= this.values.length;
					}

					if (index == 0) {
						this.start = physIdx;
						if ((this.gapStart + this.gapSize) % this.values.length == this.start) {
							this.end = this.gapStart;
							this.gapSize = 0;
						}
					}
				} else {
					int src = physIdx;
					int dst = physIdx + this.gapSize;
					int len = this.gapStart - physIdx;
					this.moveDataWithGap(src, dst, len);
					this.gapSize--;
					this.gapStart = physIdx + 1;
					this.gapIndex = index + 1;

					if (index == 0) {
						this.start = physIdx;
						this.end = physIdx;
					} else if (index == this.size) {
						if ((this.gapStart + this.gapSize) % this.values.length == this.start) {
							this.end = this.gapStart;
							this.gapSize = 0;
						}
					}
				}
			}
		}

		this.values[physIdx] = elem;
		this.size++;

		if (GapList.DEBUG_DUMP) {
			this.debugDump();
		}
		if (GapList.DEBUG_CHECK) {
			this.debugCheck();
		}

		return true;
	}

	@Override
	protected E doRemove(int index) {
		int physIdx;

		// Remove at last position
		if (index == this.size - 1) {
			if (GapList.DEBUG_TRACE) {
				this.debugLog("Case R0");
			}

			this.end--;
			if (this.end < 0) {
				this.end += this.values.length;
			}
			physIdx = this.end;

			// Remove gap if it is followed by only one element
			if (this.gapSize > 0) {
				if (this.gapIndex == index) {
					// R0-1
					this.end = this.gapStart;
					this.gapSize = 0;
				}
			}

			// Remove at first position
		} else if (index == 0) {
			if (GapList.DEBUG_TRACE) {
				this.debugLog("Case R1");
			}

			physIdx = this.start;
			this.start++;
			if (this.start >= this.values.length) {
				// R1-1
				this.start -= this.values.length;
			}

			// Remove gap if if it is preceded by only one element
			if (this.gapSize > 0) {
				if (this.gapIndex == 1) {
					this.start += this.gapSize;
					if (this.start >= this.values.length) {
						// R1-2
						this.start -= this.values.length;
					}
					this.gapSize = 0;
				} else {
					this.gapIndex--;
				}
			}
		} else {
			// Remove in middle of list
			physIdx = this.physIndex(index);

			// Create gap
			if (this.gapSize == 0) {
				if (GapList.DEBUG_TRACE) {
					this.debugLog("Case R2");
				}
				this.gapIndex = index;
				this.gapStart = physIdx;
				this.gapSize = 1;

				// Extend existing gap at tail
			} else if (index == this.gapIndex) {
				if (GapList.DEBUG_TRACE) {
					this.debugLog("Case R3");
				}
				this.gapSize++;

				// Extend existing gap at head
			} else if (index == this.gapIndex - 1) {
				if (GapList.DEBUG_TRACE) {
					this.debugLog("Case R4");
				}
				this.gapStart--;
				if (this.gapStart < 0) {
					this.gapStart += this.values.length;
				}
				this.gapSize++;
				this.gapIndex--;
			} else {
				// Move existing gap
				assert (this.gapSize > 0);

				boolean moveLeft;
				int gapEnd = (this.gapStart + this.gapSize - 1) % this.values.length + 1;
				if (gapEnd < this.gapStart) {
					// Gap is at head and tail: check where fewer
					// elements must be moved
					int len1 = physIdx - gapEnd;
					int len2 = this.gapStart - physIdx - 1;
					if (len1 <= len2) {
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case R5a");
						}
						moveLeft = true;
					} else {
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case R6a");
						}
						moveLeft = false;
					}
				} else {
					if (physIdx > this.gapStart) {
						// Existing gap is left of insertion point
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case R5b");
						}
						moveLeft = true;
					} else {
						// Existing gap is right of insertion point
						if (GapList.DEBUG_TRACE) {
							this.debugLog("Case R6b");
						}
						moveLeft = false;
					}
				}
				if (moveLeft) {
					int src = this.gapStart + this.gapSize;
					int dst = this.gapStart;
					int len = physIdx - gapEnd;
					this.moveDataWithGap(src, dst, len);
					this.gapStart += len;
					if (this.gapStart >= this.values.length) {
						this.gapStart -= this.values.length;
					}
					this.gapSize++;
				} else {
					int src = physIdx + 1;
					int dst = physIdx + this.gapSize + 1;
					int len = this.gapStart - physIdx - 1;
					this.moveDataWithGap(src, dst, len);
					this.gapStart = physIdx;
					this.gapSize++;
				}
				this.gapIndex = index;
			}
		}

		E removed = this.values[physIdx];
		this.values[physIdx] = null;
		this.size--;

		if (GapList.DEBUG_DUMP) {
			this.debugDump();
		}
		if (GapList.DEBUG_CHECK) {
			this.debugCheck();
		}
		return removed;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doEnsureCapacity(int minCapacity) {
		// Note: Same behavior as in ArrayList.ensureCapacity()
		int oldCapacity = this.values.length;
		if (minCapacity <= oldCapacity) {
			return;    // do not shrink
		}
		minCapacity = Math.max(GapList.DEFAULT_CAPACITY, minCapacity);
		int newCapacity = (oldCapacity * 3) / 2 + 1;
		if (newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}

		E[] newValues = (E[]) new Object[newCapacity];
		if (this.size == 0) {
		} else if (this.start == 0) {
			System.arraycopy(this.values, 0, newValues, 0, this.values.length);
		} else if (this.start > 0) {
			int grow = newCapacity - this.values.length;
			newValues = (E[]) new Object[newCapacity];
			System.arraycopy(this.values, 0, newValues, 0, this.start);
			System.arraycopy(this.values, this.start, newValues, this.start + grow, this.values.length - this.start);
			if (this.gapStart > this.start && this.gapSize > 0) {
				this.gapStart += grow;
			}
			if (this.end > this.start) {
				this.end += grow;
			}
			this.start += grow;
		}
		if (this.end == 0 && this.start == 0 && this.size != 0) {
			// S1, S6
			this.end = this.values.length;
		}
		this.values = newValues;

		if (GapList.DEBUG_DUMP) {
			this.debugDump();
		}
		if (GapList.DEBUG_CHECK) {
			this.debugCheck();
		}
	}

	@Override
	protected <T> void doGetAll(T[] array, int index, int len) {
		int[] physIdx = this.physIndex(index, index + len);
		int pos = 0;
		for (int i = 0; i < physIdx.length; i += 2) {
			int num = physIdx[i + 1] - physIdx[i];
			System.arraycopy(this.values, physIdx[i], array, pos, num);
			pos += num;
		}
		assert (pos == len);
	}

	@Override
	protected void doRemoveAll(int index, int len) {
		if (len == this.size()) {
			this.doModify();
			this.doClear();
		} else {
			for (int i = 0; i < len; i++) {
				this.doRemove(index);
			}
		}
	}

	/**
	 * Calculate index for physical access to an element.
	 *
	 * @param idx logical index of element
	 *
	 * @return physical index to access element in values[]
	 */
	private int physIndex(int idx) {
		int physIdx = idx + this.start;
		if (idx >= this.gapIndex) {
			physIdx += this.gapSize;
		}
		if (physIdx >= this.values.length) {
			physIdx -= this.values.length;
		}
		return physIdx;
	}


	// --- Serialization ---

	/**
	 * Calculate indexes for physical access to a range of elements.
	 * The method returns between one and three ranges of physical indexes.
	 *
	 * @param idx0 start index
	 * @param idx1 end index
	 *
	 * @return array with physical start and end indexes (may contain 2, 4, or 6 elements)
	 */
	private int[] physIndex(int idx0, int idx1) {
		assert (idx0 >= 0 && idx1 <= this.size && idx0 <= idx1);

		if (idx0 == idx1) {
			return new int[0];
		}

		// Decrement idx1 to make sure we get the physical index
		// of an existing position. We will increment the physical index
		// again before returning.
		idx1--;
		int pidx0 = this.physIndex(idx0);
		if (idx1 == idx0) {
			return new int[]{
					pidx0, pidx0 + 1
			};
		}

		int pidx1 = this.physIndex(idx1);
		if (pidx0 < pidx1) {
			if (this.gapSize > 0 && pidx0 < this.gapStart && pidx1 > this.gapStart) {
				assert (pidx0 < this.gapStart);
				assert (this.gapStart + this.gapSize < pidx1 + 1);

				return new int[]{
						pidx0, this.gapStart,
						this.gapStart + this.gapSize, pidx1 + 1
				};
			} else {
				return new int[]{
						pidx0, pidx1 + 1
				};
			}
		} else {
			assert (pidx0 > pidx1);
			assert (this.start != 0);
			if (this.gapSize > 0 && pidx1 > this.gapStart && this.gapStart > 0) {
				assert (pidx0 < this.values.length);
				assert (0 < this.gapStart);
				assert (this.gapStart + this.gapSize < pidx1 + 1);

				return new int[]{
						pidx0, this.values.length,
						0, this.gapStart,
						this.gapStart + this.gapSize, pidx1 + 1
				};
			} else if (this.gapSize > 0 && pidx0 < this.gapStart && this.gapStart + this.gapSize < this.values.length) {
				assert (pidx0 < this.gapStart);
				assert (this.gapStart + this.gapSize < this.values.length);
				assert (0 < pidx1 + 1);

				return new int[]{
						pidx0, this.gapStart,
						this.gapStart + this.gapSize, this.values.length,
						0, pidx1 + 1
				};
			} else {
				assert (pidx0 < this.values.length);
				assert (0 < pidx1 + 1);

				int end = this.values.length;
				if (this.gapSize > 0 && this.gapStart > pidx0) {
					end = this.gapStart;
				}
				int start = 0;
				if (this.gapSize > 0 && (this.gapStart + this.gapSize) % this.values.length < pidx1 + 1) {
					start = (this.gapStart + this.gapSize) % this.values.length;
				}

				return new int[]{
						pidx0, end,
						start, pidx1 + 1
				};
			}
		}
	}

	/**
	 * Normalize data of GapList so the elements are found
	 * from values[0] to values[size-1].
	 * This method can help to speed up operations like sort or
	 * binarySearch.
	 */
	private void normalize() {
		if (this.start == 0 && this.end == 0 && this.gapSize == 0 && this.gapStart == 0 && this.gapIndex == 0) {
			return;
		}
		this.init(this.toArray(), this.size());
	}

	/**
	 * Move a range of elements in the values array and adjust the gap.
	 * The elements are first copied and the source range is then
	 * filled with null.
	 *
	 * @param src start index of source range
	 * @param dst start index of destination range
	 * @param len number of elements to move
	 */
	private void moveDataWithGap(int src, int dst, int len) {
		if (GapList.DEBUG_TRACE) {
			this.debugLog("moveGap: " + src + "-" + src + len + " -> " + dst + "-" + dst + len);
		}

		if (src > this.values.length) {
			src -= this.values.length;
		}
		if (dst > this.values.length) {
			dst -= this.values.length;
		}
		assert (len >= 0);
		assert (src + len <= this.values.length);

		if (this.start >= src && this.start < src + len) {
			this.start += dst - src;
			if (this.start >= this.values.length) {
				this.start -= this.values.length;
			}
		}
		if (this.end >= src && this.end < src + len) {
			this.end += dst - src;
			if (this.end >= this.values.length) {
				this.end -= this.values.length;
			}
		}
		if (dst + len <= this.values.length) {
			this.moveData(src, dst, len);
		} else {
			// Destination range overlaps end of range so do the
			// move in two calls
			int len2 = dst + len - this.values.length;
			int len1 = len - len2;
			if (!(src <= len2 && len2 < dst)) {
				this.moveData(src + len1, 0, len2);
				this.moveData(src, dst, len1);
			} else {
				this.moveData(src, dst, len1);
				this.moveData(src + len1, 0, len2);
			}
		}
	}

	/**
	 * Move a range of elements in the values array.
	 * The elements are first copied and the source range is then
	 * filled with null.
	 *
	 * @param src start index of source range
	 * @param dst start index of destination range
	 * @param len number of elements to move
	 */
	private void moveData(int src, int dst, int len) {
		if (GapList.DEBUG_TRACE) {
			this.debugLog("moveData: " + src + "-" + src + len + " -> " + dst + "-" + dst + len);
			if (GapList.DEBUG_DUMP) {
				this.debugLog(this.debugPrint(this.values));
			}
		}
		System.arraycopy(this.values, src, this.values, dst, len);

		// Write null into array slots which are not used anymore
		// This is necessary to allow GC to reclaim non used objects.
		int start;
		int end;
		if (src <= dst) {
			start = src;
			end = (dst < src + len) ? dst : src + len;
		} else {
			start = (src > dst + len) ? src : dst + len;
			end = src + len;
		}
		// Inline of Arrays.fill
		assert (end - start <= len);
		for (int i = start; i < end; i++) {
			this.values[i] = null;
		}

		if (GapList.DEBUG_TRACE) {
			if (GapList.DEBUG_DUMP) {
				this.debugLog(this.debugPrint(this.values));
			}
		}
	}

	/**
	 * Serialize a GapList object.
	 *
	 * @param oos output stream for serialization
	 *
	 * @throws IOException if serialization fails
	 * @serialData The length of the array backing the <tt>GapList</tt>
	 * instance is emitted (int), followed by all of its elements
	 * (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(ObjectOutputStream oos) throws IOException {
		// Write out array length
		int size = this.size();
		oos.writeInt(size);

		// Write out all elements in the proper order.
		for (int i = 0; i < size; i++) {
			oos.writeObject(this.doGet(i));
		}
	}

	/**
	 * Deserialize a GapList object.
	 *
	 * @param ois input stream for serialization
	 *
	 * @throws IOException            if serialization fails
	 * @throws ClassNotFoundException if serialization fails
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Read in array length and allocate array
		this.size = ois.readInt();
		this.values = (E[]) new Object[this.size];

		// Read in all elements in the proper order.
		for (int i = 0; i < this.size; i++) {
			this.values[i] = (E) ois.readObject();
		}
	}

	// --- Helper methods for debugging ---

	/**
	 * Private method to check invariant of GapList.
	 * It is only used for debugging.
	 */
	private void debugCheck() {
		// If the GapList is not used for storing content in KeyListImpl, values may be null
		if (this.values == null) {
			assert (this.size == 0 && this.start == 0 && this.end == 0);
			assert (this.gapSize == 0 && this.gapStart == 0 && this.gapIndex == 0);
			return;
		}

		assert (this.size >= 0 && this.size <= this.values.length);
		assert (this.start >= 0 && (this.start < this.values.length || this.values.length == 0));
		assert (this.end >= 0 && (this.end < this.values.length || this.values.length == 0));
		assert (this.values.length == 0 || (this.start + this.size + this.gapSize) % this.values.length == this.end);

		// Check that logical gap index is correct
		assert (this.gapSize >= 0);
		if (this.gapSize > 0) {
			assert (this.gapStart >= 0 && this.gapStart < this.values.length);
			// gap may not be at start or end
			assert (this.gapIndex > 0 && this.gapIndex < this.size);
			// gap start may not be the same as start or end
			assert (this.gapStart != this.start && this.gapStart != this.end);
			// check that logical and phyiscal gap index are correct
			assert (this.physIndex(this.gapIndex) == (this.gapStart + this.gapSize) % this.values.length);
		}

		// Check that gap positions contain null values
		if (this.gapSize > 0) {
			for (int i = this.gapStart; i < this.gapStart + this.gapSize; i++) {
				int pos = (i % this.values.length);
				assert (this.values[pos] == null);
			}
		}

		// Check that all end positions contain null values
		if (this.start < this.end) {
			for (int i = 0; i < this.start; i++) {
				assert (this.values[i] == null);
			}
			for (int i = this.end; i < this.values.length; i++) {
				assert (this.values[i] == null);
			}
		} else if (this.end < this.start) {
			for (int i = this.end; i < this.start; i++) {
				assert (this.values[i] == null);
			}
		}
	}

	/**
	 * Private method to determine state of GapList.
	 * It is only used for debugging.
	 *
	 * @return state in which GapList is
	 */
	private int debugState() {
		if (this.size == 0) {
			return 0;
		} else if (this.size == this.values.length) {
			return 1;
		} else if (this.gapSize == 0) {
			if (this.start == 0) {
				return 2;
			} else if (this.end == 0) {
				return 3;
			} else if (this.start < this.end) {
				return 4;
			} else if (this.start > this.end) {
				return 5;
			}
		} else if (this.gapSize > 0) {
			if (this.start == this.end) {
				if (this.start == 0) {
					return 6;
				} else if (this.gapStart < this.start) {
					return 7;
				} else if (this.gapStart > this.start) {
					int gapEnd = (this.gapStart + this.gapSize) % this.values.length;
					if (gapEnd > this.gapStart) {
						return 8;
					} else if (gapEnd < this.gapStart) {
						return 9;
					}
				}
			} else if (this.start != this.end) {
				if (this.start == 0) {
					return 10;
				} else if (this.gapStart < this.start) {
					return 14; //
				} else if (this.gapStart > this.start) {
					int gapEnd = (this.gapStart + this.gapSize) % this.values.length;
					if (gapEnd < this.gapStart) {
						return 12;
					} else {
						if (this.end == 0) {
							return 11;
						} else if (this.end > this.start) {
							return 13;
						} else if (this.end < this.start) {
							return 15;
						}
					}
				}
			}
		}
		assert (false);
		return -1;
	}

	/**
	 * Private method to dump fields of GapList.
	 * It is only called if the code is run in development mode.
	 */
	private void debugDump() {
		this.debugLog("values: size= " + this.values.length + ", data= " + this.debugPrint(this.values));
		this.debugLog("size=" + this.size + ", start=" + this.start + ", end=" + this.end +
					  ", gapStart=" + this.gapStart + ", gapSize=" + this.gapSize + ", gapIndex=" + this.gapIndex);
		this.debugLog(this.toString());
	}

	/**
	 * Print array values into string.
	 *
	 * @param values array with values
	 *
	 * @return string representing array values
	 */
	private String debugPrint(E[] values) {
		StringBuilder buf = new StringBuilder();
		buf.append("[ ");
		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				buf.append(", ");
			}
			buf.append(values[i]);
		}
		buf.append(" ]");
		return buf.toString();
	}


	/**
	 * Private method write logging output.
	 * It is only used for debugging.
	 *
	 * @param msg message to write out
	 */
	private void debugLog(String msg) {
	}

	// --- ImmutableGapList ---

	/**
	 * An immutable version of a GapList.
	 * Note that the client cannot change the list,
	 * but the content may change if the underlying list is changed.
	 */
	protected static class ImmutableGapList<E> extends GapList<E> {

		/**
		 * UID for serialization
		 */
		private static final long serialVersionUID = -1352274047348922584L;

		/**
		 * Private constructor used internally.
		 *
		 * @param that list to create an immutable view of
		 */
		protected ImmutableGapList(GapList<E> that) {
			super(true, that);
		}

		@Override
		protected boolean doAdd(int index, E elem) {
			this.error();
			return false;
		}

		@Override
		protected E doSet(int index, E elem) {
			this.error();
			return null;
		}

		@Override
		protected E doReSet(int index, E elem) {
			this.error();
			return null;
		}

		@Override
		protected E doRemove(int index) {
			this.error();
			return null;
		}

		@Override
		protected void doRemoveAll(int index, int len) {
			this.error();
		}

		@Override
		protected void doClear() {
			this.error();
		}

		@Override
		protected void doModify() {
			this.error();
		}

		/**
		 * Throw exception if an attempt is made to change an immutable list.
		 */
		private void error() {
			throw new UnsupportedOperationException("list is immutable");
		}
	}
}
