package ilarkesto.mda.legacy.model;

public class ReferencePropertyModel extends SimplePropertyModel {

	private EntityModel referencedEntity;
	private boolean master;
	private BackReferenceModel backReference;

	public ReferencePropertyModel(BeanModel entityModel, String name, EntityModel referencedEntity) {
		super(entityModel, name, true, false, referencedEntity.getPackageName() + "." + referencedEntity.getName());
		this.referencedEntity = referencedEntity;
	}

	@Override
	public EntityModel getReferencedEntity() {
		return referencedEntity;
	}

	public ReferencePropertyModel createBackReference(String name) {
		if (!getBean().isEntity()) return this;
		backReference = new BackReferenceModel(name, this);
		referencedEntity.addBackReference(backReference);
		return this;
	}

	public ReferencePropertyModel setBackReferenceName(String name) {
		backReference.setName(name);
		return this;
	}

	public ReferencePropertyModel setMaster(boolean master) {
		if (master && (!isReference() || isCollection()))
			throw new RuntimeException("Only a simple reference property can be a master");
		this.master = master;
		setMandatory(master);
		return this;
	}

	public boolean isMaster() {
		return master;
	}

}
