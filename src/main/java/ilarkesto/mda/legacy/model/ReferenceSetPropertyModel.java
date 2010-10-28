package ilarkesto.mda.legacy.model;

public class ReferenceSetPropertyModel extends SetPropertyModel {

	private EntityModel referencedEntity;
	private BackReferenceModel backReference;

	public ReferenceSetPropertyModel(BeanModel entityModel, String name, EntityModel referencedEntity) {
		super(entityModel, name, true, false, referencedEntity.getBeanClass());
		this.referencedEntity = referencedEntity;
	}

	public ReferenceSetPropertyModel createBackReference(String name) {
		if (!getBean().isEntity()) return this;
		backReference = new BackReferenceModel(name, this);
		referencedEntity.addBackReference(backReference);
		return this;
	}

	public EntityModel getReferencedEntity() {
		return referencedEntity;
	}

	public ReferenceSetPropertyModel setBackReferenceName(String name) {
		backReference.setName(name);
		return this;
	}

}
