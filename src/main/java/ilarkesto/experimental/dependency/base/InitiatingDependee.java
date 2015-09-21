package ilarkesto.experimental.dependency.base;

import java.util.HashSet;
import java.util.Set;

public class InitiatingDependee<Valuetype> implements Dependee<Valuetype> {

	private Valuetype value;
	private Set<Depender> dependers;

	public InitiatingDependee() {
		dependers = new HashSet<Depender>();
	}

	public final void setValue(Valuetype value) {
		if (value.equals(this.value)) return;
		this.value = value;
		for (Depender depender : dependers) {
			depender.markDirty();
		}
	}

	@Override
	public final Valuetype getValue(Depender depender) {
		if (depender != null) dependers.add(depender);
		return value;
	}

}
