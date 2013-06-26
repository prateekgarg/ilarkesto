package ilarkesto.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AJsonWrapperList<I extends AJsonWrapper> extends AJsonWrapper implements Iterable<I> {

	public static final String ITEMS_PROPERTY = "items";

	private Class<I> itemType;

	public AJsonWrapperList(Class<I> itemType, JsonObject json) {
		super(json);
		this.itemType = itemType;
	}

	public AJsonWrapperList(Class<I> itemType) {
		this.itemType = itemType;
	}

	public final void set(JsonObject json) {
		List<JsonObject> jsonItems = json.getArrayOfObjects(ITEMS_PROPERTY);
		List<I> items = AJsonWrapper.getAsWrapperList(jsonItems, itemType);
		set(items);
	}

	public final void set(Collection<I> items) {
		json.put(ITEMS_PROPERTY, AJsonWrapper.getJsonObjects(items));
	}

	public final List<I> getAll() {
		return getWrapperArray(ITEMS_PROPERTY, itemType);
	}

	public final boolean add(I item) {
		return getAll().add(item);
	}

	public final boolean addAll(Collection<I> items) {
		return getAll().addAll(items);
	}

	public final boolean contains(I item) {
		return getAll().contains(item);
	}

	public final boolean remove(I item) {
		return getAll().remove(item);
	}

	public final int size() {
		return getAll().size();
	}

	public final boolean isEmpty() {
		return getAll().isEmpty();
	}

	public final void clear() {
		getAll().clear();
	}

	@Override
	public Iterator<I> iterator() {
		return getAll().iterator();
	}

}
