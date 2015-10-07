package ilarkesto.functional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class Stream<A> {

	protected abstract A next();

	protected abstract boolean hasNext();

	private static class InitialStream<A> extends Stream<A> {

		private Iterator<A> source;

		private InitialStream(Iterator<A> source) {
			this.source = source;
		}

		@Override
		protected A next() {
			return source.next();
		}

		@Override
		protected boolean hasNext() {
			return source.hasNext();
		}
	}

	private static class MapStream<A, B> extends Stream<B> {

		private Stream<A> upstream;
		private Function<A, B> mapping;

		private MapStream(Stream<A> upstream, Function<A, B> mapping) {
			this.upstream = upstream;
			this.mapping = mapping;
		}

		@Override
		protected B next() {
			return mapping.apply(upstream.next());
		}

		@Override
		protected boolean hasNext() {
			return upstream.hasNext();
		}

	}

	private static class FilterStream<A> extends Stream<A> {

		private Stream<A> upstream;
		private Predicate<A> survivesFilter;

		private FilterStream(Stream<A> upstream, Predicate<A> survivesFilter) {
			this.upstream = upstream;
			this.survivesFilter = survivesFilter;
		}

		private A nextElement;
		private boolean nextPeeked;

		@Override
		protected A next() {
			if (!nextPeeked) {
				if (!hasNext()) throw new NoSuchElementException();
			}
			nextPeeked = false;
			return nextElement;
		}

		@Override
		protected boolean hasNext() {
			while (upstream.hasNext()) {
				nextElement = upstream.next();
				if (survivesFilter.test(nextElement)) {
					nextPeeked = true;
					return true;
				}
			}
			return false;
		}

	}

	public static <A> Stream<A> start(Iterable<A> source) {
		return new InitialStream<A>(source.iterator());
	}

	public <B> Stream<B> map(Function<A, B> mapping) {
		return new MapStream<A, B>(this, mapping);
	}

	public Stream<A> filter(Predicate<A> survivesFilter) {
		return new FilterStream<A>(this, survivesFilter);
	}

	public A reduce(A identity, BiFunction<A, A, A> accumulator) {
		A result = identity;
		while (hasNext()) {
			result = accumulator.apply(result, next());
		}
		return result;
	}

	public List<A> list() {
		List<A> result = new ArrayList<A>();
		while (hasNext()) {
			result.add(next());
		}
		return result;
	}

}
