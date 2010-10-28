package ilarkesto.mda.legacy.model;

public class BackReferenceModel extends AModel {

	private PropertyModel reference;

	public BackReferenceModel(String name, PropertyModel reference) {
		super(name);
		this.reference = reference;
	}

	public PropertyModel getReference() {
		return reference;
	}

}
