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
package ilarkesto.integration.vlc;

import ilarkesto.base.Proc;
import ilarkesto.base.Str;
import ilarkesto.base.Utl;
import ilarkesto.core.logging.Log;
import ilarkesto.media.player.APlayer;
import ilarkesto.media.player.PlayerState;

public class VlcPlayer extends APlayer {

	public static void main(String[] args) {
		VlcPlayer player = new VlcPlayer();
		player.play("http://www.dradio.de/streaming/dlf.m3u");
		Utl.sleep(5000);
		player.togglePause();
		Utl.sleep(2000);
		player.togglePause();
		Utl.sleep(5000);
		player.stop();
	}

	private static final long COMMAND_WAIT_TIME = 1000;
	private static Log log = Log.get(VlcPlayer.class);

	private String vlcPath = "vlc";
	private Proc vlcProc;

	@Override
	public synchronized void play(String url) {
		sendCommand("add", url);
		setState(new PlayerState(url, true));
	}

	@Override
	public synchronized void stop() {
		if (isVlcRunning()) {
			sendCommand("quit");
			vlcProc.destroy();
			vlcProc = null;
		}
		setState(new PlayerState(null, false));
	}

	@Override
	public synchronized void pause() {
		if (isVlcRunning()) sendCommand("pause");
		setState(new PlayerState(state.getUrl(), false));
	}

	@Override
	public void resume() {
		sendCommand("play");
		setState(new PlayerState(state.getUrl(), true));
	}

	private synchronized void sendCommand(String... s) {
		startVlcProcess();
		String command = Str.concat(s, " ");
		log.info("Sending command:", command);
		vlcProc.sendInputLine(command);
		// Utl.sleep(COMMAND_WAIT_TIME);
		log.debug("    VLC output:", vlcProc.popOutput());
	}

	private synchronized void startVlcProcess() {
		if (vlcProc != null) return;
		log.info("Starting VLC");
		vlcProc = new Proc(vlcPath);
		vlcProc.addParameters("--intf", "rc");
		vlcProc.start();
		Utl.sleep(COMMAND_WAIT_TIME);
		log.debug("    VLC output:", vlcProc.popOutput());
	}

	public boolean isVlcRunning() {
		return vlcProc != null && vlcProc.isRunning();
	}
}
