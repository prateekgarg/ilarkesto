package ilarkesto.android;

import android.content.Context;

public class Localizer {

	private Context context;

	public Localizer(Context context) {
		super();
		this.context = context;
	}

	@Deprecated
	public String text(int id) {
		return Android.text(context, id);
	}

	@Deprecated
	public String text(int id, Object... params) {
		return Android.text(context, id, params);
	}
}
