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

import ilarkesto.base.Utl;
import ilarkesto.integration.vlc.VlcPlayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlaylistPlayer {

	public static void main(String[] args) {
		PlaylistPlayer player = new PlaylistPlayer(new VlcPlayer());
		player.play("http://www.dradio.de/streaming/dlf.m3u");
		player.appendItem("http://www.dradio.de/streaming/dkultur.m3u");
		Utl.sleep(5000);
		player.playNext();
		Utl.sleep(5000);
		player.stop();
		System.exit(0);
	}

	private APlayer player;
	private PlaylistPlayerState state;
	private List<PlaylistPlayerObserver> observers = new LinkedList<PlaylistPlayerObserver>();

	private List<String> previousItems = new LinkedList<String>();
	private String currentItem;
	private List<String> nextItems = new LinkedList<String>();

	public PlaylistPlayer(APlayer player) {
		super();
		this.player = player;
		currentItem = player.getState().getUrl();
		updateState();
	}

	// state changing methods

	public synchronized void play() {
		if (currentItem == null) moveToNextItem();
		if (currentItem == null) {
			if (previousItems.isEmpty()) {
				player.stop();
				updateState();
				return;
			}
			player.stop();
			updateState();
			return;
		}
		player.play(currentItem);
		updateState();
	}

	public void stop() {
		player.stop();
		updateState();
	}

	public void pause() {
		player.pause();
		updateState();
	}

	public void resume() {
		player.resume();
		updateState();
	}

	public void togglePause() {
		player.togglePause();
		updateState();
	}

	public synchronized void appendAsNext(String url) {
		if (currentItem == null) {
			currentItem = url;
			updateState();
			return;
		}
		nextItems.add(0, url);
		updateState();
	}

	public synchronized void appendItem(String url) {
		nextItems.add(url);
		updateState();
	}

	//

	public synchronized void playPrevious() {
		moveToPreviousItem();
		if (currentItem == null) {
			stop();
			return;
		}
		play();
	}

	public synchronized void play(String url) {
		appendAsNext(url);
		playNext();
	}

	public synchronized void playNext() {
		moveToNextItem();
		play();
	}

	private synchronized void moveToPreviousItem() {
		if (currentItem != null) {
			nextItems.add(0, currentItem);
		}
		if (previousItems.isEmpty()) {
			currentItem = null;
			return;
		}
		int index = previousItems.size() - 1;
		currentItem = previousItems.get(index);
		previousItems.remove(index);
	}

	private synchronized void moveToNextItem() {
		if (currentItem != null) {
			previousItems.add(currentItem);
		}
		if (nextItems.isEmpty()) {
			currentItem = null;
			return;
		}
		currentItem = nextItems.get(0);
		nextItems.remove(0);
	}

	public APlayer getPlayer() {
		return player;
	}

	public PlaylistPlayerState getState() {
		return state;
	}

	protected final void updateState() {
		PlayerState playerState = player.getState();
		this.state = new PlaylistPlayerState(playerState.isPlaying(), new ArrayList<String>(previousItems),
				currentItem, new ArrayList<String>(nextItems));
		for (PlaylistPlayerObserver observer : observers) {
			observer.onStateChanged(this, state);
		}
	}

	public void addObserver(PlaylistPlayerObserver observer) {
		observers.add(observer);
	}

}
