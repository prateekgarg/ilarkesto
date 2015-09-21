package ilarkesto.experimental.dependency.base;

import java.util.Stack;

public class DependerStack {

	private static DependerStack stack;

	public static DependerStack get() {
		if (stack == null) {
			stack = new DependerStack();
		}
		return stack;
	}

	private DependerStack() {
		dependers = new Stack<Depender>();
	}

	private Stack<Depender> dependers;

	public Depender peek() {
		if (dependers.isEmpty()) return null;
		return dependers.peek();
	}

	public Depender pop() {
		if (dependers.isEmpty()) return null;
		return dependers.pop();
	}

	public void push(Depender depender) {
		dependers.push(depender);
	}

}
