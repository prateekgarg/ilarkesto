package ilarkesto.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

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
		if (editor != null) editorSetCode(editor, text);
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
		String selection = editorSelection(editor);
		if (selection == null) selection = "";
		selection = prefix + selection + suffix;
		editorReplaceSelection(editor, selection);
	}

	public String getSelectedText() {
		if (editor == null) return null;
		return editorSelection(editor);
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

	private native String editorSelection(JavaScriptObject jsEditor)
	/*-{
		return jsEditor.selection();
	}-*/;

	private native String editorGetCode(JavaScriptObject jsEditor)
	/*-{
		return jsEditor.getCode();
	}-*/;

	private native void editorSetCode(JavaScriptObject jsEditor, String text)
	/*-{
		jsEditor.setCode(text);
	}-*/;

	private native void editorReplaceSelection(JavaScriptObject jsEditor, String text)
	/*-{
		jsEditor.replaceSelection(text);
	}-*/;

	private native void editorFocus(JavaScriptObject jsEditor)
	/*-{
		jsEditor.focus();
	}-*/;

}
