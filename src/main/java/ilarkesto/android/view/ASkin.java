package ilarkesto.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

public class ASkin {

	protected Context context;

	public Color colorTitleBackground;

	public ASkin(Context context) {
		super();
		this.context = context;
		initialize(context.getResources());
	}

	protected void initialize(Resources resources) {
		// colorTitleBackground = new Color(1f, 1f, 1f);
	}

}
