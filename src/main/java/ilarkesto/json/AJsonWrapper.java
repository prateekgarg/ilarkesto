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
import java.util.Collection;
import java.util.LinkedList;
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

	protected Integer getMandatoryInteger(String name) {
		return assertNotNull(name, json.getInteger(name));
	}

	protected Long getMandatoryLong(String name) {
		return assertNotNull(name, json.getLong(name));
	}

	protected Float getMandatoryFloat(String name) {
		return assertNotNull(name, json.getFloat(name));
	}

	private <T> T assertNotNull(String name, T value) {
		if (value == null)
			throw new IllegalStateException("Mandatory property \"" + name + "\" does not exist: " + json);
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

	protected <T extends AJsonWrapper> List<T> getWrapperArray(String name, Class<T> type) {
		// TODO list caching
		List<JsonObject> array = json.getArrayOfObjects(name);
		if (array == null) {
			json.put(name, new LinkedList<JsonObject>());
			return getWrapperArray(name, type);
		}
		return new JsonWrapperList<T>(type, array);
	}

	protected <T extends AJsonWrapper> T getWrapper(String name, Class<T> type) {
		// TODO wrapper caching
		return getAsWrapper(json, name, type);
	}

	@Override
	public int hashCode() {
		return json.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof AJsonWrapper)) return false;
		return json.equals(((AJsonWrapper) obj).json);
	}

	@Override
	public JsonObject getJson() {
		return json;
	}

	@Override
	public String toString() {
		return json.toFormatedString();
	}

	// --- static helper ---

	static List<JsonObject> getJsonObjects(Collection<? extends AJsonWrapper> wrappers) {
		List<JsonObject> ret = new ArrayList<JsonObject>(wrappers.size());
		for (AJsonWrapper wrapper : wrappers) {
			ret.add(wrapper.json);
		}
		return ret;
	}

	static <T extends AJsonWrapper> T getAsWrapper(JsonObject json, String name, Class<T> type) {
		JsonObject object = json.getObject(name);
		if (object == null) return null;
		return createWrapper(object, type);
	}

	static <T extends AJsonWrapper> List<T> getAsWrapperList(Collection<JsonObject> jsonObjects, Class<T> type) {
		if (jsonObjects == null || jsonObjects.isEmpty()) return new LinkedList<T>();
		List<T> ret = new ArrayList<T>(jsonObjects.size());
		for (JsonObject json : jsonObjects) {
			ret.add(createWrapper(json, type));
		}
		return ret;
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

}
