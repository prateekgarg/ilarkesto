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
package ilarkesto.integration.mediaplayer;

import ilarkesto.base.Utl;
import ilarkesto.integration.mediaplayer.vlc.VlcPlayer;

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

	private List<String> previousItems = new LinkedList<String>();
	private String currentItem;
	private List<String> nextItems = new LinkedList<String>();

	public PlaylistPlayer(APlayer player) {
		super();
		this.player = player;
	}

	public synchronized void play(String url) {
		appendAsNext(url);
		playNext();
	}

	public synchronized void play() {
		if (currentItem == null) moveToNextItem();
		if (currentItem == null) {
			if (previousItems.isEmpty()) {
				player.stop();
				return;
			}
			player.stop();
			return;
		}
		player.play(currentItem);
	}

	public synchronized void playNext() {
		moveToNextItem();
		play();
	}

	public synchronized void playPrevious() {
		moveToPreviousItem();
		if (currentItem == null) {
			player.stop();
			return;
		}
		player.play(currentItem);
	}

	public void stop() {
		player.stop();
	}

	public void pause() {
		player.pause();
	}

	public void resume() {
		player.resume();
	}

	public void togglePause() {
		player.togglePause();
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

	public synchronized void appendAsNext(String url) {
		nextItems.add(0, url);
	}

	public synchronized void appendItem(String url) {
		nextItems.add(url);
	}

	public APlayer getPlayer() {
		return player;
	}

}
