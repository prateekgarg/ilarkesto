package ilarkesto.integration.ffmpeg;

import ilarkesto.base.Proc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FfmpegCmd {

	private Proc proc = new Proc("ffmpeg");
	private int returnCode;
	private String output;

	public void execute() {
		if (proc == null) throw new IllegalStateException("ffmpeg already executed");
		proc.start();
		proc.waitFor();
		returnCode = proc.getReturnCode();
		output = proc.getOutput();
		proc = null;
	}

	public List<String> getStreamInfos() {
		List<String> ret = new ArrayList<String>(3);
		StringTokenizer tokenizer = new StringTokenizer(getOutput(), "\r\n");
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();
			line = line.trim();
			if (line.startsWith("Stream #")) ret.add(line);
		}
		return ret;
	}

	public String getOutput() {
		if (proc != null) throw new IllegalStateException("ffmpeg not executed yet");
		return output;
	}

	public void addInputFile(File file) {
		proc.addParameters("-i", file.getAbsolutePath());
	}

	public int getReturnCode() {
		if (proc != null) throw new IllegalStateException("ffmpeg not executed yet");
		return returnCode;
	}

	public static List<String> getStreamInfos(File file) {
		FfmpegCmd ffmpeg = new FfmpegCmd();
		ffmpeg.addInputFile(file);
		ffmpeg.execute();
		return ffmpeg.getStreamInfos();
	}

}
