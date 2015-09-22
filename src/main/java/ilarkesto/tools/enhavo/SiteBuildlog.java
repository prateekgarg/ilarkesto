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
package ilarkesto.tools.enhavo;

import ilarkesto.io.IO;
import ilarkesto.json.JsonObject;
import ilarkesto.protocol.AProtocolConsumer;
import ilarkesto.protocol.Message;
import ilarkesto.protocol.Message.Type;

import java.io.File;
import java.util.List;

public class SiteBuildlog extends AProtocolConsumer {

	private SiteContext siteContext;

	private JsonObject jStatus;

	public SiteBuildlog(SiteContext siteContext) {
		super();
		this.siteContext = siteContext;
	}

	public void onBuildStart() {
		jStatus = new JsonObject();
	}

	public void onBuildSuccess() {
		jStatus.put("success", true);
		writeBuildlog();
	}

	@Override
	public void onMessage(Message message, List<String> currentContext) {
		if (jStatus == null) return;

		JsonObject jMessage = new JsonObject();
		jMessage.put("text", message.getText());
		jMessage.put("type", message.getType().name());
		jMessage.put("depth", currentContext.size());
		jStatus.addToArray("messages", jMessage);

		writeBuildlog();
	}

	@Override
	public void onContextStart(String context, List<String> currentContext) {
		onMessage(new Message(Type.INFO, context), currentContext);
	}

	@Override
	public void onContextEnd(String context, List<String> currentContext) {}

	private void writeBuildlog() {
		jStatus.put("timestamp", System.currentTimeMillis());
		File file = siteContext.getOutputFile(".buildlog.json");
		IO.writeFile(file, jStatus.toString(), IO.UTF_8);
	}

}
