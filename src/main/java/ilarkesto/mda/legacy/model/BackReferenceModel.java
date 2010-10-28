package ilarkesto.mda.legacy.model;

public class BackReferenceModel extends AModel {

	private ReferencePropertyModel reference;

	public BackReferenceModel(String name, ReferencePropertyModel reference) {
		super(name);
		this.reference = reference;
	}

	public ReferencePropertyModel getReference() {
		return reference;
	}

}
