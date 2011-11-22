/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.itext;

import ilarkesto.core.logging.Log;
import ilarkesto.pdf.AImage;
import ilarkesto.pdf.AParagraph;
import ilarkesto.pdf.AParagraphElement;
import ilarkesto.pdf.APdfElement;
import ilarkesto.pdf.FontStyle;
import ilarkesto.pdf.TextChunk;

import java.io.File;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.FontSelector;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class Paragraph extends AParagraph implements ItextElement {

	private static Log log = Log.get(Paragraph.class);

	public Paragraph(APdfElement parent) {
		super(parent);
	}

	@Override
	public Element getITextElement() {
		com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph();
		float maxSize = 0;
		for (AParagraphElement element : getElements()) {
			if (element instanceof TextChunk) {
				TextChunk textChunk = (TextChunk) element;
				FontStyle fontStyle = textChunk.getFontStyle();

				FontSelector fontSelector = createFontSelector(fontStyle.getFont(), fontStyle);

				String text = textChunk.getText();
				Phrase phrase = fontSelector.process(text);
				p.add(phrase);

				float size = (fontStyle.getSize() * 1.1f) + 1f;
				if (size > maxSize) maxSize = PdfBuilder.mmToPoints(size);
			} else if (element instanceof Image) {
				Image image = (Image) element;
				com.lowagie.text.Image itextImage;
				try {
					itextImage = image.getITextElement();
				} catch (Exception ex) {
					log.warn("Including image failed:", image, ex);
					continue;
				}

				if (image.getAlign() != null) {
					itextImage.setAlignment(Image.convertAlign(image.getAlign()) | com.lowagie.text.Image.TEXTWRAP);
					p.add(itextImage);
				} else {
					Chunk chunk = new Chunk(itextImage, 0, 0);
					p.add(chunk);
					float size = image.getHeight() + 3;
					if (size > maxSize) maxSize = size;
				}

			} else {
				throw new RuntimeException("Unsupported paragraph element: " + element.getClass().getName());
			}
		}
		p.setLeading(maxSize);
		p.setSpacingBefore(PdfBuilder.mmToPoints(spacingTop));
		p.setSpacingAfter(PdfBuilder.mmToPoints(spacingBottom));
		if (align != null) p.setAlignment(convertAlign(align));
		if (height <= 0) return p;

		// wrap in table
		PdfPCell cell = new PdfPCell();
		cell.setBorder(0);
		cell.setFixedHeight(PdfBuilder.mmToPoints(height));
		cell.addElement(p);
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.addCell(cell);
		return table;
	}

	private int createStyle(FontStyle fontStyle) {
		int style = Font.NORMAL;
		if (fontStyle.isItalic() && fontStyle.isBold()) {
			style = Font.BOLDITALIC;
		} else if (fontStyle.isItalic()) {
			style = Font.ITALIC;
		} else if (fontStyle.isBold()) {
			style = Font.BOLD;
		}
		return style;
	}

	public FontSelector createFontSelector(String preferredFont, FontStyle fontStyle) {
		FontSelector selector = new FontSelector();
		selector.addFont(createFont(preferredFont, fontStyle));

		// fallback from ilarkesto.jar
		selector.addFont(createFont("fonts/HDZB_36.ttf", fontStyle)); // embeddable chinese

		// fallback from iTextAsian.jar
		selector.addFont(createFont("STSong-Light", fontStyle)); // simplified chinese
		selector.addFont(createFont("MHei-Medium", fontStyle)); // traditional chinese
		selector.addFont(createFont("HeiseiMin-W3", fontStyle)); // japanese
		selector.addFont(createFont("KozMinPro-Regular", fontStyle)); // japanese
		selector.addFont(createFont("HYGoThic-Medium", fontStyle)); // korean

		return selector;
	}

	private Font createFont(String name, FontStyle fontStyle) {
		Font font;
		try {
			font = new Font(BaseFont.createFont(name, BaseFont.IDENTITY_H, BaseFont.EMBEDDED));
		} catch (Exception ex) {
			throw new RuntimeException("Loading font failed: " + name, ex);
		}

		if (fontStyle != null) {
			font.setStyle(createStyle(fontStyle));
			font.setSize(PdfBuilder.mmToPoints(fontStyle.getSize()));
			font.setColor(fontStyle.getColor());
		}

		return font;
	}

	@Override
	public AImage image(byte[] data) {
		Image i = new Image(this, data);
		elements.add(i);
		return i;
	}

	@Override
	public AImage image(File file) {
		Image i = new Image(this, file);
		elements.add(i);
		return i;
	}

	private static int convertAlign(Align align) {
		switch (align) {
			case LEFT:
				return com.lowagie.text.Paragraph.ALIGN_LEFT;
			case CENTER:
				return com.lowagie.text.Paragraph.ALIGN_CENTER;
			case RIGHT:
				return com.lowagie.text.Paragraph.ALIGN_RIGHT;
		}
		throw new RuntimeException("Unsupported align: " + align);
	}

	// --- dependencies ---

}
