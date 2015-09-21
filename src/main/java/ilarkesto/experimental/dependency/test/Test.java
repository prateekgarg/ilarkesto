package ilarkesto.experimental.dependency.test;

import ilarkesto.experimental.dependency.entity.EntityA;

public class Test {

	public static void main(String[] args) {
		EntityA a = new EntityA();
		EntityA b = new EntityA();

		System.out.println();
		System.out.println();

		System.out.println("a.getY(): " + a.getY());
		System.out.println("b.getY(): " + b.getY());

		System.out.println();
		System.out.println();

		a.setX(b);

		System.out.println();
		System.out.println();

		System.out.println("a.getY(): " + a.getY());
		System.out.println("b.getY(): " + b.getY());

		System.out.println();
		System.out.println();

		System.out.println();
		System.out.println();

		System.out.println("a.getY(): " + a.getY());
		System.out.println("b.getY(): " + b.getY());

		System.out.println();
		System.out.println();

		b.setX(a);

		System.out.println();
		System.out.println();

		System.out.println("a.getY(): " + a.getY());
		System.out.println("b.getY(): " + b.getY());
	}

}
