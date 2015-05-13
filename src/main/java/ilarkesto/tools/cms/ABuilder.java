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
package ilarkesto.tools.cms;

public abstract class ABuilder {

	protected CmsContext cms;

	protected abstract void onBuild();

	public ABuilder(CmsContext cms) {
		super();
		this.cms = cms;
	}

	public final void build() {
		cms.getProt().pushContext(toString());
		try {
			onBuild();
		} catch (Exception ex) {
			error(ex);
		} finally {
			cms.getProt().popContext();
		}
	}

	protected void info(Object... message) {
		cms.getProt().info(message);
	}

	protected void error(Object... message) {
		cms.getProt().error(message);
	}

}
