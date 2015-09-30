/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.gwt.client.integration.ace;

import ilarkesto.core.logging.Log;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AceEditor implements IsWidget {

	private static final Log log = Log.get(AceEditor.class);

	private static int lastId = 0;
	private static boolean loaded = false;

	private String id;
	private SimplePanel div;

	public AceEditor() {
		id = "aceEditor_" + (++lastId);
	}

	@Override
	public Widget asWidget() {
		if (div == null) {
			div = new SimplePanel();
			div.getElement().setId(id);
			Style style = div.getElement().getStyle();
			// style.setPosition(Position.ABSOLUTE);
			// style.setTop(0, Unit.PX);
			// style.setRight(0, Unit.PX);
			// style.setBottom(0, Unit.PX);
			// style.setLeft(0, Unit.PX);
			style.setWidth(800, Unit.PX);
			// style.setHeight(400, Unit.PX);
			style.setBackgroundColor("yellow");
			div.addAttachHandler(new AttachEvent.Handler() {

				@Override
				public void onAttachOrDetach(AttachEvent event) {
					if (event.isAttached()) {
						activate();
					}
				}
			});
		}
		return div;
	}

	private void activate() {
		if (loaded) {
			new Timer() {

				@Override
				public void run() {
					activateAce(div.getElement());
				}

			}.schedule(2000);
			return;
		}
		ScriptInjector.fromUrl("js/ace/ace.js").setCallback(new Callback<Void, Exception>() {

			@Override
			public void onFailure(Exception ex) {
				throw new RuntimeException("Loading ACE failed.", ex);
			}

			@Override
			public void onSuccess(Void result) {
				loaded = true;
				activate();
			}
		}).inject();
	}

	private static native void activateAce(Element element)
	/*-{
	console.log("Activating ACE:",element);
	var editor = ace.edit(element);
	editor.setTheme("ace/theme/monokai");
	editor.getSession().setMode("ace/mode/javascript");
	editor.resize();
	}-*/;

}
