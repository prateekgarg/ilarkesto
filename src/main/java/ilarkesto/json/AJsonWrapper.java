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
package ilarkesto.json;

import ilarkesto.core.base.Utl;
import ilarkesto.json.Json.JsonWrapper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AJsonWrapper implements JsonWrapper {

	protected final JsonObject json;

	public AJsonWrapper(JsonObject json) {
		if (json == null) throw new IllegalArgumentException("json == null");
		this.json = json;
	}

	public AJsonWrapper() {
		this(new JsonObject());
	}

	protected void putMandatory(String name, Object value) {
		if (value == null) throw new IllegalArgumentException("Mandatory property \"" + name + "\" is null.");
		json.put(name, value);
	}

	protected String getMandatoryString(String name) {
		return assertNotNull(name, json.getString(name));
	}

	private <T> T assertNotNull(String name, T value) {
		if (value == null)
			throw new IllegalStateException("Mandatory property \"" + name + "\" does not exist: " + toString());
		return value;
	}

	protected boolean checkEquals(Object obj, String... properties) {
		if (obj == null) return false;
		if (!getClass().equals(obj.getClass())) return false;
		AJsonWrapper other = (AJsonWrapper) obj;
		if (properties.length == 0) return json.equals(other.json);
		for (String property : properties) {
			if (!Utl.equals(json.get(property), other.json.getString(property))) return false;
		}
		return true;
	}

	protected int hashCode(String... properties) {
		if (properties.length == 0) return json.hashCode();
		int hash = 1;
		for (String property : properties) {
			Object value = json.get(property);
			if (value == null) {
				hash = hash * 13 + property.hashCode();
				continue;
			}
			hash = hash * 17 + value.hashCode();
		}
		return hash;
	}

	protected <P extends AJsonWrapper> P getParent(Class<P> type) {
		return createWrapper(json.getParent(), type);
	}

	protected void putArray(String name, Iterable<? extends AJsonWrapper> wrappers) {
		json.put(name, getJsonObjects(wrappers));
	}

	protected static List<JsonObject> getJsonObjects(Iterable<? extends AJsonWrapper> wrappers) {
		List<JsonObject> ret = new ArrayList<JsonObject>();
		for (AJsonWrapper wrapper : wrappers) {
			ret.add(wrapper.getJson());
		}
		return ret;
	}

	protected void putOrRemove(String name, String value) {
		if (value == null) {
			json.remove(name);
		} else {
			json.put(name, value);
		}
	}

	protected void putOrRemove(String name, boolean value) {
		if (value) {
			json.put(name, value);
		} else {
			json.remove(name);
		}
	}

	protected <T extends AJsonWrapper> List<T> createFromArray(String name, Class<T> type) {
		return getArrayAsWrapperList(json, name, type);
	}

	protected <T extends AJsonWrapper> T createFromObject(String name, Class<T> type) {
		return getAsWrapper(json, name, type);
	}

	public static <T extends AJsonWrapper> T getAsWrapper(JsonObject json, String name, Class<T> type) {
		JsonObject object = json.getObject(name);
		if (object == null) return null;
		return createWrapper(object, type);
	}

	public static <T extends AJsonWrapper> List<T> getArrayAsWrapperList(JsonObject json, String name, Class<T> type) {
		if (json == null) return Collections.emptyList();
		List<JsonObject> array = json.getArrayOfObjects(name);
		if (array == null || array.isEmpty()) return Collections.emptyList();

		List<T> wrappers = new ArrayList<T>(array.size());
		for (JsonObject object : array) {
			T wrapper = createWrapper(object, type);
			wrappers.add(wrapper);
		}

		return wrappers;
	}

	public static <T extends AJsonWrapper> T createWrapper(JsonObject json, Class<T> type) {
		if (json == null) return null;
		Constructor<T> constructor;
		try {
			constructor = type.getConstructor(JsonObject.class);
		} catch (Exception ex) {
			throw new RuntimeException("Loading constructor for " + type.getName() + " failed.", ex);
		}
		T wrapper;
		try {
			wrapper = constructor.newInstance(json);
		} catch (Exception ex) {
			throw new RuntimeException("Instantiating " + type.getName() + " failed.", ex);
		}
		return wrapper;
	}

	@Override
	public JsonObject getJson() {
		return json;
	}

	@Override
	public String toString() {
		return json.toFormatedString();
	}

}
