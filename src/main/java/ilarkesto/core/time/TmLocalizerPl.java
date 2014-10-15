package ilarkesto.core.time;

public class TmLocalizerPl extends TmLocalizer {

	public static final TmLocalizerPl INSTANCE = new TmLocalizerPl();

	@Override
	public String date(int year, int month, int day) {
		return prefixZeros(day, 2) + "." + prefixZeros(month, 2) + "." + year;
	}

	@Override
	public String monthDay(int month, int day) {
		return prefixZeros(day, 2) + "." + prefixZeros(month, 2);
	}

	@Override
	public String full(Weekday day) {
		switch (day) {
			case MONDAY:
				return "Poniedziałek";
			case TUESDAY:
				return "Wtorek";
			case WEDNESDAY:
				return "Środa";
			case THURSDAY:
				return "Czwartek";
			case FRIDAY:
				return "Piątek";
			case SATURDAY:
				return "Sobota";
			case SUNDAY:
				return "Niedziela";
		}
		throw new IllegalStateException(day.name());
	}

	@Override
	public String shorted(Weekday day) {
		switch (day) {
			case MONDAY:
				return "Po";
			case TUESDAY:
				return "Wt";
			case WEDNESDAY:
				return "Śr";
			case THURSDAY:
				return "Cz";
			case FRIDAY:
				return "Pi";
			case SATURDAY:
				return "So";
			case SUNDAY:
				return "Ni";
		}
		throw new IllegalStateException(day.name());
	}

	@Override
	public String years(long count) {
		return "Lat";
	}

	@Override
	public String months(long count) {
		return "mie.";
	}

	@Override
	public String weeks(long count) {
		return "tyg.";
	}

	public String days(int dayCount) {
		return "dni";
	}

	@Override
	public String hours(long count) {
		return "godz.";
	}

	@Override
	public String minutes(long count) {
		return "min.";
	}

	@Override
	public String seconds(long count) {
		return "sek.";
	}

	@Override
	public String millis(long count) {
		return "ms";
	}

}
