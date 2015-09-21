package ilarkesto.experimental.dependency.entity;

import ilarkesto.experimental.dependency.base.Dependee;
import ilarkesto.experimental.dependency.base.Dependency;
import ilarkesto.experimental.dependency.base.DependerStack;
import ilarkesto.experimental.dependency.base.InitiatingDependee;

public abstract class GEntityA {

	private InitiatingDependee<EntityA> xDependee;
	private Dependee<String> yDependee;

	public GEntityA() {
		xDependee = new InitiatingDependee<EntityA>();
		yDependee = new Dependency<String>() {

			@Override
			public String evaluate() {
				return calculateY();
			}
		};
	}

	protected abstract String calculateY();

	public void setX(EntityA x) {
		xDependee.setValue(x);
	}

	public final EntityA getX() {
		return xDependee.getValue(DependerStack.get().peek());
	}

	public final String getY() {
		return yDependee.getValue(DependerStack.get().peek());
	}

}
