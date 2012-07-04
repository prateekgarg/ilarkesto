package ilarkesto.core.time;

public class TmLocalizerDe extends TmLocalizer {

	@Override
	public String full(Weekday day) {
		switch (day) {
			case MONDAY:
				return "Montag";
			case TUESDAY:
				return "Dienstag";
			case WEDNESDAY:
				return "Mittwoch";
			case THURSDAY:
				return "Donnerstag";
			case FRIDAY:
				return "Freitag";
			case SATURDAY:
				return "Samstag";
			case SUNDAY:
				return "Sonntag";
		}
		throw new IllegalStateException(day.name());
	}

	@Override
	public String years(long count) {
		return "Jahr.";
	}

	@Override
	public String months(long count) {
		return "Mon.";
	}

	@Override
	public String weeks(long count) {
		return "Wo.";
	}

	@Override
	public String days(long count) {
		return "Tag.";
	}

	@Override
	public String hours(long count) {
		return "Std.";
	}

	@Override
	public String minutes(long count) {
		return "Min.";
	}

	@Override
	public String seconds(long count) {
		return "Sek.";
	}

	@Override
	public String millis(long count) {
		return "ms";
	}

}
