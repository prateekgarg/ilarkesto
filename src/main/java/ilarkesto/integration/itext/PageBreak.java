package ilarkesto.integration.itext;

import ilarkesto.pdf.APageBreak;
import ilarkesto.pdf.APdfElement;

import com.lowagie.text.Element;

public class PageBreak extends APageBreak implements ItextElement {

	public PageBreak(APdfElement parent) {
		super(parent);
	}

	@Override
	public Element getITextElement() {
		throw new IllegalStateException();
	}

}
