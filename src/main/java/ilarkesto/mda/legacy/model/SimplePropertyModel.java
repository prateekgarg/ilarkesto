package ilarkesto.mda.legacy.model;

public class SimplePropertyModel extends PropertyModel {

	private String type;
	private boolean optionRestricted;

	public SimplePropertyModel(BeanModel entityModel, String name, boolean reference, boolean valueObject, String type) {
		super(entityModel, name, reference, valueObject);
		this.type = type;
	}

	public SimplePropertyModel setOptionRestricted(boolean optioned) {
		this.optionRestricted = optioned;
		return this;
	}

	@Override
	public boolean isOptionRestricted() {
		return optionRestricted;
	}

	public EntityModel getReferencedEntity() {
		return null;
	}

	@Override
	public final String getType() {
		return type;
	}

	@Override
	public String getContentType() {
		return getType();
	}

	@Override
	public String getCollectionType() {
		throw new UnsupportedOperationException("not a collection property");
	}

	@Override
	public String getCollectionImpl() {
		throw new UnsupportedOperationException("not a collection property");
	}

	@Override
	public final boolean isCollection() {
		return false;
	}

	@Override
	public String getNameSingular() {
		return getName();
	}

	@Override
	public boolean isBoolean() {
		return "boolean".equals(getType()) || Boolean.class.getName().equals(getType());
	}

	@Override
	public boolean isString() {
		return String.class.getName().equals(getType());
	}

	@Override
	public boolean isPrimitive() {
		if (isBoolean()) return true;
		if (int.class.getName().equals(type)) return true;
		if (long.class.getName().equals(type)) return true;
		return false;
	}

}
