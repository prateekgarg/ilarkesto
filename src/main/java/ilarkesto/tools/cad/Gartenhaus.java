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
package ilarkesto.tools.cad;

import ilarkesto.base.Sys;
import ilarkesto.integration.svg.Point;
import ilarkesto.io.IO;
import ilarkesto.tools.cad.stores.Glass;
import ilarkesto.tools.cad.stores.WoodBeam;
import ilarkesto.ui.web.HtmlBuilder;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Gartenhaus extends ADesign {

	Size balkenStaerke;

	Size fensterBreite;
	Size fensterVorneHoehe;

	Size glasKlemmbreite;
	Size glasBreite;
	Size glasVorneHoehe;

	Size fensterbankHoehe;

	int fensterVorneAnzahl;
	int fensterSeiteAnzahl;

	Size breite;
	Size tiefe;
	Size pfostenVorneHoehe;

	private Front front;

	public Gartenhaus() {
		super("Gartenhaus");

		balkenStaerke = new Size("Balken Stärke", 10);

		fensterBreite = new Size("Fenster Breite", 90);
		fensterVorneHoehe = new Size("Fenster vorne Höhe", 150);

		glasKlemmbreite = new Size("Glas Klemmbreite", 2);
		glasBreite = new Size("Glas Breite", fensterBreite.getValue().add(
			glasKlemmbreite.getValue().multiply(new BigDecimal(2))));
		glasVorneHoehe = new Size("Glas vorne Höhe", fensterVorneHoehe.getValue().add(
			glasKlemmbreite.getValue().multiply(new BigDecimal(2))));

		fensterVorneAnzahl = 4;
		fensterSeiteAnzahl = 2;
		breite = new Size("Breite", fensterBreite.getValue().multiply(new BigDecimal(fensterVorneAnzahl))
				.add(balkenStaerke.getValue().multiply(new BigDecimal(fensterVorneAnzahl + 1))));
		tiefe = new Size("Tiefe", fensterBreite.getValue().multiply(new BigDecimal(fensterSeiteAnzahl))
				.add(balkenStaerke.getValue().multiply(new BigDecimal(fensterSeiteAnzahl + 1))));
		pfostenVorneHoehe = new Size("Pfosten vorne Höhe", 200);

		fensterbankHoehe = new Size("Fensterbank Höhe", pfostenVorneHoehe.getValue().subtract(
			fensterVorneHoehe.getValue()));

		front = new Front();
	}

	class Front extends ADesign {

		Artefact balkenOben;
		List<Artefact> pfostens;
		List<Artefact> traegers;
		List<Artefact> fensters;

		public Front() {
			super("Front");

			WoodBeam beam = new WoodBeam().setSize(balkenStaerke.getValue());
			Glass glass = new Glass().setWidth(glasBreite.getValue());

			Point cursor = new Point(0, 0);
			beam.setHorizontal().setLength(breite.getValue());
			balkenOben = beam.create("Balken oben", cursor);

			pfostens = new ArrayList<Artefact>();
			beam.setVertical().setLength(pfostenVorneHoehe.getValue());
			cursor = balkenOben.getSvgElement().below();
			for (int i = 0; i <= fensterVorneAnzahl; i++) {
				Artefact pfosten = beam.create("Pfosten " + (i + 1), cursor);
				pfostens.add(pfosten);
				cursor = cursor.right(balkenStaerke.getValue()).right(fensterBreite.getValue());
			}

			traegers = new ArrayList<Artefact>();
			beam.setHorizontal().setLength(fensterBreite.getValue());
			cursor = balkenOben.getSvgElement().below().right(balkenStaerke.getValue())
					.down(fensterVorneHoehe.getValue());
			for (int i = 0; i < fensterVorneAnzahl; i++) {
				Artefact traeger = beam.create("Träger " + (i + 1), cursor);
				traegers.add(traeger);
				cursor = cursor.right(balkenStaerke.getValue()).right(traeger.getSvgElement().getWidth());
			}

			fensters = new ArrayList<Artefact>();
			glass.setHeight(glasVorneHoehe.getValue());
			for (int i = 0; i < fensterVorneAnzahl; i++) {
				cursor = traegers.get(i).getSvgElement().getPosition().left(glasKlemmbreite.getValue())
						.up(glasVorneHoehe.getValue()).down(glasKlemmbreite.getValue());
				Artefact fenster = glass.create("Fenster " + (i + 1), cursor);
				fensters.add(fenster);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Gartenhaus gartenhaus = new Gartenhaus();

		File file = new File(Sys.getUsersHomePath() + "/inbox/gartenhaus.html");
		HtmlBuilder html = new HtmlBuilder(file, IO.UTF_8);
		html.startHTML();
		html.startBODY();
		gartenhaus.buildHtml(html);
		html.endBODY();
		html.endHTML();
		html.close();
	}

}
