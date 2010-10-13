package ilarkesto.gwt.client;

import ilarkesto.core.base.Str;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

// http://codemirror.net/manual.html
public class CodemirrorEditorWidget extends AWidget {

	private TextArea textArea = new MyTextArea();
	private JavaScriptObject editor;
	private String height = "200px";

	private native JavaScriptObject createEditor(String textAreaId, String height, String text)
	/*-{
		var editor = new $wnd.CodeMirror($wnd.CodeMirror.replace(textAreaId), {
			parserfile: ["parsewiki.js"],
			path: "codemirror/js/",
			stylesheet: "codemirror/css/wikicolors.css",
			height: height,
		    continuousScanning: 1000,
		    lineNumbers: false,
		    readOnly: false,
		    textWrapping: true,
		    tabMode: "spaces",
		    content: text		
		});
		editor.ensureWindowLoaded = function() {
			if (this.editor == null) alert("Waiting for internal frame to load. This is a temporary workaround.");
		}
		return editor;
	}-*/;

	@Override
	protected Widget onInitialization() {
		// Log.DEBUG("--------------------------------- onInitialization() -------------------------------");
		return textArea;
	}

	private void createEditor() {
		if (editor == null) {
			String text = textArea.getText();
			editor = createEditor(textArea.getElement().getId(), height, Str.isBlank(text) ? "\n" : text);
		}
	}

	public void setText(String text) {
		textArea.setText(text);
		if (editor != null) setCode(editor, text);
	}

	public void focus() {
		if (editor != null) focus(editor);
	}

	public void addKeyPressHandler(KeyPressHandler listener) {
		// TODO
	}

	public String getText() {
		if (editor == null) return textArea.getText();
		String text = editorGetCode(editor);
		textArea.setText(text);
		return text;
	}

	@Override
	public void setHeight(String height) {
		this.height = height;
	}

	public void wrapSelection(String prefix, String suffix) {
		wrapSelection(editor, prefix, suffix);
	}

	public void wrapLine(String prefix, String suffix) {
		wrapLine(editor, prefix, suffix);
	}

	public String getSelectedText() {
		if (editor == null) return null;
		return selection(editor);
	}

	private native String selection(JavaScriptObject editor)
	/*-{
	    editor.ensureWindowLoaded();
		return editor.selection();
	}-*/;

	private native String editorGetCode(JavaScriptObject editor)
	/*-{
	    editor.ensureWindowLoaded();
		return editor.getCode();
	}-*/;

	private native void setCode(JavaScriptObject editor, String text)
	/*-{
	    editor.ensureWindowLoaded();
		editor.setCode(text);
	}-*/;

	private native void wrapLine(JavaScriptObject editor, String prefix, String suffix)
	/*-{
	    editor.ensureWindowLoaded();
	    cursorPosition = editor.cursorPosition(true);
	    selection = editor.selection();
	    if (selection==null) selection = "";
	    line = editor.lineContent(cursorPosition.line); 
		editor.setLineContent(cursorPosition.line, prefix + line + suffix);
		from = cursorPosition.character+prefix.length;
		to = cursorPosition.character+prefix.length+selection.length;
		editor.selectLines(cursorPosition.line, from, cursorPosition.line, to);
	}-*/;

	private native void wrapSelection(JavaScriptObject editor, String prefix, String suffix)
	/*-{
	    editor.ensureWindowLoaded();
	    cursorPosition = editor.cursorPosition(true);
	    selection = editor.selection(); 
	    if (selection==null) selection = "";
		editor.replaceSelection(prefix + selection + suffix);
		from = cursorPosition.character+prefix.length;
		to = cursorPosition.character+prefix.length+selection.length;
		editor.selectLines(cursorPosition.line, from, cursorPosition.line, to);
	}-*/;

	private native void focus(JavaScriptObject editor)
	/*-{
	    editor.ensureWindowLoaded();
		editor.focus();
	}-*/;

	private class MyTextArea extends TextArea {

		public MyTextArea() {
			setWidth("100%");
			getElement().setId("CodeMirror" + System.currentTimeMillis());
			setVisible(false);
		}

		@Override
		protected void onAttach() {
			super.onAttach();
			createEditor();
		}

		@Override
		protected void onDetach() {
			textArea.setText(editorGetCode(editor));
			editor = null;
			super.onDetach();
		}

	}

}
