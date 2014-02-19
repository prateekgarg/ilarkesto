/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class ASearcher<I> {

	private int minQueryLength = 1;
	private int minQueryTokenLength = 1;

	public void search(String query, SearchConsumer<I> consumer) {
		if (query == null) {
			consumer.onSearchFinished();
			return;
		}

		query = query.trim();
		if (query.length() < getMinQueryLenght()) {
			consumer.onSearchFinished();
			return;
		}

		List<String> queryTokens = tokenizeQuery(query);
		// remove too short words
		int minWordLenght = getMinQueryTokenLength();
		Iterator<String> iterator = queryTokens.iterator();
		while (iterator.hasNext()) {
			String word = iterator.next();
			if (word.length() < minWordLenght) iterator.remove();
		}

		if (queryTokens.isEmpty()) {
			consumer.onSearchFinished();
			return;
		}

		try {
			search(queryTokens, consumer);
		} finally {
			consumer.onSearchFinished();
		}
	}

	protected void search(List<String> queryTokens, SearchConsumer<I> consumer) {
		Collection<I> items = getPotentialItems();
		for (I item : items) {
			if (consumer.isAbort()) return;
			if (isItemMatchingQueryTokens(queryTokens, item)) consumer.onItemFound(item);
		}
	}

	protected boolean isItemMatchingQueryTokens(List<String> queryTokens, I item) {
		for (String queryToken : queryTokens) {
			if (!isItemMatchingQueryToken(queryToken, item)) return false;
		}
		return true;
	}

	protected boolean isItemMatchingQueryToken(String queryToken, I item) {
		throw new IllegalStateException(getClass().getSimpleName()
				+ " needs to override isMatchingQueryToken(String, I)");
	}

	protected final boolean isValueMatchingQueryToken(String token, Object... values) {
		for (Object value : values) {
			if (value == null) continue;
			String s = value.toString().toLowerCase();
			if (s.contains(token)) return true;
		}
		return false;
	}

	protected Collection<I> getPotentialItems() {
		throw new IllegalStateException(getClass().getSimpleName() + " needs to override loadPotentialItems()");
	}

	private final int getMinQueryLenght() {
		return minQueryLength;
	}

	public ASearcher<I> setMinQueryLength(int minQueryLength) {
		this.minQueryLength = minQueryLength;
		return this;
	}

	private int getMinQueryTokenLength() {
		return minQueryTokenLength;
	}

	public ASearcher<I> setMinQueryTokenLength(int minQueryTokenLength) {
		this.minQueryTokenLength = minQueryTokenLength;
		return this;
	}

	protected List<String> tokenizeQuery(String query) {
		List<String> ret = new LinkedList<String>();
		StringTokenizer tokenizer = new StringTokenizer(query);
		while (tokenizer.hasMoreTokens()) {
			ret.add(tokenizer.nextToken().toLowerCase());
		}
		return ret;
	}

	public final List<I> searchAndGetResults(String query) {
		final List<I> ret = new ArrayList<I>();
		search(query, new SearchConsumer<I>() {

			@Override
			public void onItemFound(I item) {
				ret.add(item);
			}
		});
		return ret;
	}

}
