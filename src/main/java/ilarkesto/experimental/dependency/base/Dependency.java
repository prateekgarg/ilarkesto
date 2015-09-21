package ilarkesto.experimental.dependency.base;

import java.util.HashSet;
import java.util.Set;

public abstract class Dependency<ValueType> implements Depender, Dependee<ValueType> {

	private Set<Depender> dependers;
	private boolean dirty = true;
	private boolean isGetting = false;
	private ValueType cachedValue;

	public Dependency() {
		dependers = new HashSet<Depender>();
	}

	public abstract ValueType evaluate();

	@Override
	public final ValueType getValue(Depender depender) {
		if (isGetting) throw new IllegalStateException("Circular Dependency detected!");
		isGetting = true;
		if (depender != null) dependers.add(depender);
		if (dirty) {
			DependerStack.get().push(this);
			cachedValue = evaluate();
			DependerStack.get().pop();
			dirty = false;
		}
		isGetting = false;
		return cachedValue;
	}

	@Override
	public final void markDirty() {
		if (dirty) return;
		dirty = true;
		for (Depender depender : dependers) {
			depender.markDirty();
		}
	}

}
