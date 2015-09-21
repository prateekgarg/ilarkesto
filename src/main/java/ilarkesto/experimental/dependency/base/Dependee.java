package ilarkesto.experimental.dependency.base;

public interface Dependee<ValueType> {

	public ValueType getValue(Depender depender);

}
