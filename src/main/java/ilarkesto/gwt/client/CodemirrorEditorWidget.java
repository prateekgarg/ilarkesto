package ilarkesto.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

// http://codemirror.net/manual.html
public class CodemirrorEditorWidget extends AWidget {

	private TextArea textArea;
	private JavaScriptObject editor;
	private String height = "200px";

	public CodemirrorEditorWidget() {}

	@Override
	protected Widget onInitialization() {
		textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.getElement().setId("CodeMirror" + System.currentTimeMillis());
		return textArea;
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if (editor == null) editor = createEditor(textArea.getElement().getId(), height, textArea.getText());
	}

	public void setText(String text) {
		textArea.setText(text);
		if (editor != null) setCode(editor, text);
	}

	public void focus() {
		// if (editor != null) editorFocus(editor);
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
		    textWrapping: false,
		    tabMode: "spaces",
		    content: text		
		});
		return editor;
	}-*/;

	private native String selection(JavaScriptObject editor)
	/*-{
		return editor.selection();
	}-*/;

	private native String editorGetCode(JavaScriptObject editor)
	/*-{
		return editor.getCode();
	}-*/;

	private native void setCode(JavaScriptObject editor, String text)
	/*-{
		editor.setCode(text);
	}-*/;

	private native void wrapLine(JavaScriptObject editor, String prefix, String suffix)
	/*-{
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
		editor.focus();
	}-*/;

}
