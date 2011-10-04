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
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.media.player;

import java.util.ArrayList;
import java.util.List;

public abstract class APlayer {

	private List<PlayerObserver> observers = new ArrayList<PlayerObserver>();

	protected PlayerState state = new PlayerState(null, false);

	public abstract void play(String url);

	public abstract void pause();

	public abstract void resume();

	public abstract void stop();

	protected final void setState(PlayerState state) {
		this.state = state;
		for (PlayerObserver observer : observers) {
			observer.onStateChanged(this, state);
		}
	}

	public synchronized final void togglePause() {
		if (getState().isPlaying()) {
			pause();
		} else {
			resume();
		}
	}

	public final PlayerState getState() {
		return state;
	}

	public void addObserver(PlayerObserver observer) {
		observers.add(observer);
	}

}
