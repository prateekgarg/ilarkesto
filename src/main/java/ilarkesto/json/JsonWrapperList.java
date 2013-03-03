package ilarkesto.json;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

public class JsonWrapperList<I extends AJsonWrapper> extends AbstractList<I> implements List<I> {

	public static final String DEFAULT_ITEMS_PROPERTY = "items";

	private Class<I> type;
	private List<JsonObject> list;

	public JsonWrapperList(Class<I> itemType, List<JsonObject> jsonList) {
		if (jsonList == null) throw new IllegalArgumentException("jsonList == null");
		this.type = itemType;
		this.list = jsonList;
	}

	public JsonWrapperList(Class<I> itemType, JsonObject json, String listProperty) {
		this(itemType, json.getArrayOfObjects(listProperty));
	}

	public JsonWrapperList(Class<I> itemType, JsonObject json) {
		this(itemType, json, DEFAULT_ITEMS_PROPERTY);
	}

	@Override
	public I get(int index) {
		JsonObject json = list.get(index);
		if (json == null) return null;
		return AJsonWrapper.createWrapper(json, type);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean add(I e) {
		return list.add(e.json);
	}

	@Override
	public void add(int index, I element) {
		list.add(index, element.json);
	}

	@Override
	public boolean addAll(Collection<? extends I> c) {
		return list.addAll(AJsonWrapper.getJsonObjects(c));
	}

	@Override
	public boolean addAll(int index, Collection<? extends I> c) {
		return list.addAll(index, AJsonWrapper.getJsonObjects(c));
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(((AJsonWrapper) o).json);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(AJsonWrapper.getJsonObjects((Iterable<? extends AJsonWrapper>) c));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof JsonWrapperList)) return false;
		return list.equals(AJsonWrapper.getJsonObjects((Iterable<? extends AJsonWrapper>) obj));
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(((AJsonWrapper) o).json);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(((AJsonWrapper) o).json);
	}

	@Override
	public I remove(int index) {
		return AJsonWrapper.createWrapper(list.remove(index), type);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(((AJsonWrapper) o).json);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(AJsonWrapper.getJsonObjects((Iterable<? extends AJsonWrapper>) c));
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(AJsonWrapper.getJsonObjects((Iterable<? extends AJsonWrapper>) c));
	}

	@Override
	public I set(int index, I element) {
		return AJsonWrapper.createWrapper(list.set(index, element == null ? null : element.json), type);
	}

	@Override
	public List<I> subList(int fromIndex, int toIndex) {
		List<JsonObject> sublist = list.subList(fromIndex, toIndex);
		return new JsonWrapperList<I>(type, sublist);
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = get(i);
		}
		return array;
	}

}
