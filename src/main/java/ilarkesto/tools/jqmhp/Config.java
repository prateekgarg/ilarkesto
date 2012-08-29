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
package ilarkesto.tools.jqmhp;

import ilarkesto.integration.jquery.JqueryMobileDownloader;
import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.io.File;

public class Config extends AJsonWrapper {

	public Config(JsonObject json) {
		super(json);
		if (!json.contains("jqm")) json.put("jqm", new JsonObject());
		if (!json.contains("content")) json.put("content", new JsonObject());
	}

	public Jqm getJqm() {
		return createFromObject("jqm", Jqm.class);
	}

	public Content getContent() {
		return createFromObject("content", Content.class);
	}

	public static class Content extends AJsonWrapper {

		public Content(JsonObject json) {
			super(json);
		}

		public String getTitle() {
			return json.getString("title", "JqmHp");
		}

	}

	public static class Jqm extends AJsonWrapper {

		public Jqm(JsonObject json) {
			super(json);
		}

		public String getVersion() {
			return json.getString("version", JqueryMobileDownloader.getStableVersion());
		}

		public String getJqueryVersion() {
			return JqueryMobileDownloader.getCompatibleJqueryVersion(getVersion());
		}

	}

	public static Config load(File file) {
		return new Config(new JsonObject(file));
	}

}
