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
	Size fensterHintenObenHoehe;

	Size glasKlemmbreite;
	Size glasBreite;
	Size glasVorneHoehe;
	Size glasHintenObenHoehe;

	Size fensterbankHoehe;

	transient int fensterVorneAnzahl;
	transient int fensterSeiteAnzahl;

	Size pfostenVorneHoehe;
	Size pfostenHintenHoehe;

	Size breite;
	Size tiefe;

	transient WoodBeam beam;
	transient Glass glass;

	private Vorne vorne;
	private Hinten hinten;
	private Rechts rechts;

	public Gartenhaus() {
		super("Gewächshaus");

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

		fensterbankHoehe = new Size("Fensterbank Höhe", 50);
		pfostenVorneHoehe = new Size("Pfosten vorne Höhe", fensterbankHoehe.getValue()
				.add(fensterVorneHoehe.getValue()));

		pfostenHintenHoehe = new Size("Pfosten hinten Höhe", 250);

		fensterHintenObenHoehe = new Size("Fenster hinten oben Höhe", pfostenHintenHoehe.getValue().subtract(
			fensterbankHoehe.getValue().add(fensterVorneHoehe.getValue()).add(balkenStaerke.getValue())));
		glasHintenObenHoehe = new Size("Glas hinten oben Höhe", fensterHintenObenHoehe.getValue().add(
			glasKlemmbreite.getValue().multiply(new BigDecimal(2))));

		beam = new WoodBeam().setSize(balkenStaerke.getValue());
		glass = new Glass().setWidth(glasBreite.getValue());

		vorne = new Vorne();
		hinten = new Hinten();
		rechts = new Rechts();
	}

	class Rechts extends ADesign {

		Artefact pfostenVorne;
		Artefact pfostenMitte;
		Artefact pfostenHinten;

		Artefact traegerVorne;
		Artefact traegerHinten;

		Artefact traegerOben;

		List<Artefact> untenTraegers;
		List<Artefact> untenFensters;

		public Rechts() {
			super("Rechts");

			Point cursor = new Point(0, 0);
			beam.setVertical().setLength(breite.getValue());

			beam.setLength(pfostenHintenHoehe.getValue());
			cursor = cursor.right(tiefe.getValue()).left(balkenStaerke.getValue()).down(balkenStaerke.getValue());
			pfostenHinten = beam.create("Pfosten hinten", cursor);

			beam.setLength(pfostenVorneHoehe.getValue());
			cursor = cursor.left(fensterBreite.getValue()).left(balkenStaerke.getValue())
					.down(fensterHintenObenHoehe.getValue()).down(balkenStaerke.getValue());
			pfostenMitte = beam.create("Pfosten mitte", cursor);

			beam.setLength(pfostenVorneHoehe.getValue());
			cursor = cursor.left(fensterBreite.getValue()).left(balkenStaerke.getValue());
			pfostenVorne = beam.create("Pfosten vorne", cursor);

			beam.setLength(balkenStaerke.getValue());
			cursor = pfostenVorne.getSvgElement().getPosition().up(balkenStaerke.getValue());
			traegerVorne = beam.create("Träger vorne", cursor);

			beam.setLength(balkenStaerke.getValue());
			cursor = pfostenHinten.getSvgElement().getPosition().up(balkenStaerke.getValue());
			traegerHinten = beam.create("Träger hinten", cursor);

			beam.setHorizontal().setLength(
				fensterBreite.getValue().multiply(new BigDecimal(fensterSeiteAnzahl)).add(balkenStaerke.getValue()));
			cursor = traegerVorne.getSvgElement().right();
			traegerOben = beam.create("Träger oben", cursor);

			untenTraegers = new ArrayList<Artefact>();
			beam.setHorizontal().setLength(fensterBreite.getValue());
			cursor = traegerOben.getSvgElement().below().down(fensterVorneHoehe.getValue());
			for (int i = 0; i < fensterSeiteAnzahl; i++) {
				Artefact traeger = beam.create("Träger unten " + (i + 1), cursor);
				untenTraegers.add(traeger);
				cursor = cursor.right(balkenStaerke.getValue()).right(traeger.getSvgElement().getWidth());
			}

			untenFensters = new ArrayList<Artefact>();
			glass.setHeight(glasVorneHoehe.getValue());
			for (int i = 0; i < fensterSeiteAnzahl; i++) {
				cursor = untenTraegers.get(i).getSvgElement().getPosition().left(glasKlemmbreite.getValue())
						.up(glasVorneHoehe.getValue()).down(glasKlemmbreite.getValue());
				Artefact fenster = glass.create("Fenster unten " + (i + 1), cursor);
				untenFensters.add(fenster);
			}
		}
	}

	class Hinten extends ADesign {

		Artefact balkenOben;
		List<Artefact> pfostens;
		List<Artefact> obenTraegers;
		List<Artefact> obenFensters;
		List<Artefact> untenTraegers;
		List<Artefact> untenFensters;

		public Hinten() {
			super("Hinten");

			Point cursor = new Point(0, 0);
			beam.setHorizontal().setLength(breite.getValue());
			balkenOben = beam.create("Balken oben", cursor);

			pfostens = new ArrayList<Artefact>();
			beam.setVertical().setLength(pfostenHintenHoehe.getValue());
			cursor = balkenOben.getSvgElement().below();
			for (int i = 0; i <= fensterVorneAnzahl; i++) {
				Artefact pfosten = beam.create("Pfosten " + (i + 1), cursor);
				pfostens.add(pfosten);
				cursor = cursor.right(balkenStaerke.getValue()).right(fensterBreite.getValue());
			}

			obenTraegers = new ArrayList<Artefact>();
			beam.setHorizontal().setLength(fensterBreite.getValue());
			cursor = balkenOben.getSvgElement().below().down(fensterHintenObenHoehe.getValue())
					.right(balkenStaerke.getValue());
			for (int i = 0; i < fensterVorneAnzahl; i++) {
				Artefact traeger = beam.create("Träger oben " + (i + 1), cursor);
				obenTraegers.add(traeger);
				cursor = cursor.right(balkenStaerke.getValue()).right(traeger.getSvgElement().getWidth());
			}

			obenFensters = new ArrayList<Artefact>();
			glass.setHeight(glasHintenObenHoehe.getValue());
			for (int i = 0; i < fensterVorneAnzahl; i++) {
				cursor = obenTraegers.get(i).getSvgElement().getPosition().left(glasKlemmbreite.getValue())
						.up(glasHintenObenHoehe.getValue()).down(glasKlemmbreite.getValue());
				Artefact fenster = glass.create("Fenster oben " + (i + 1), cursor);
				obenFensters.add(fenster);
			}

			untenTraegers = new ArrayList<Artefact>();
			beam.setHorizontal().setLength(fensterBreite.getValue());
			cursor = balkenOben.getSvgElement().below().down(fensterHintenObenHoehe.getValue())
					.down(balkenStaerke.getValue()).down(fensterVorneHoehe.getValue()).right(balkenStaerke.getValue());
			for (int i = 0; i < fensterVorneAnzahl; i++) {
				Artefact traeger = beam.create("Träger unten " + (i + 1), cursor);
				untenTraegers.add(traeger);
				cursor = cursor.right(balkenStaerke.getValue()).right(traeger.getSvgElement().getWidth());
			}

			untenFensters = new ArrayList<Artefact>();
			glass.setHeight(glasVorneHoehe.getValue());
			for (int i = 0; i < fensterVorneAnzahl; i++) {
				cursor = untenTraegers.get(i).getSvgElement().getPosition().left(glasKlemmbreite.getValue())
						.up(glasVorneHoehe.getValue()).down(glasKlemmbreite.getValue());
				Artefact fenster = glass.create("Fenster unten " + (i + 1), cursor);
				untenFensters.add(fenster);
			}

			untenTraegers.remove(2);
			untenFensters.remove(2);
		}
	}

	class Vorne extends ADesign {

		Artefact balkenOben;
		List<Artefact> pfostens;
		List<Artefact> traegers;
		List<Artefact> fensters;

		public Vorne() {
			super("Vorne");

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
