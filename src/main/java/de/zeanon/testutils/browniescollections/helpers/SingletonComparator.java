/*
 * Copyright 2015 by Thomas Mauch
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
 * $Id: SingletonComparator.java 4520 2019-09-21 00:41:58Z origo $
 *
 * Copied by Zeanon to reduce final jar size
 */
package de.zeanon.testutils.browniescollections.helpers;

import java.io.Serializable;
import java.util.Comparator;


/**
 * Abstract base class for implementing a singleton comparator.
 *
 * @author Thomas Mauch
 * @version $Id: SingletonComparator.java 4520 2019-09-21 00:41:58Z origo $
 */
@SuppressWarnings("ALL")
abstract class SingletonComparator<T> implements Comparator<T>, Serializable {

	@Override
	public int hashCode() {
		return getClass().getName().hashCode();
	}

	@Override
	public boolean equals(Object that) {
		if (this == null || that == null) {
			return this == that;
		}
		return this.getClass().getName().equals(that.getClass().getName());
	}
}
