package ilarkesto.experimental.dependency.entity;

public class EntityA extends GEntityA {

	@Override
	protected final String calculateY() {
		System.out.println(toString() + ".calculateY");
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (getX() == null) {
			return "1";
		} else {
			return "0" + getX().getY();
		}
	}

}
