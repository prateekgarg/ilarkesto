package ilarkesto.integration.soundunwound;

import ilarkesto.core.base.Str;

public class SoundunwoundRecord {

	private String id;
	private String title;
	private String artist;
	private Integer year;
	private String coverId;

	public SoundunwoundRecord(String id, String title, String artist, Integer year, String coverId) {
		super();
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.year = year;
		this.coverId = coverId;
	}

	public String getId() {
		return id;
	}

	public String getArtist() {
		return artist;
	}

	public boolean isArtistSet() {
		return !Str.isBlank(artist);
	}

	public String getTitle() {
		return title;
	}

	public boolean isTitleSet() {
		return !Str.isBlank(title);
	}

	public Integer getYear() {
		return year;
	}

	public boolean isYearSet() {
		return year != null;
	}

	public String getCoverId() {
		return coverId;
	}

	public boolean isCoverIdSet() {
		return !Str.isBlank(coverId);
	}

	@Override
	public String toString() {
		return title + " by " + artist + " (" + year + ")";
	}

}
