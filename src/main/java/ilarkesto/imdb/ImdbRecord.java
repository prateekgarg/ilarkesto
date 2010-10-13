package ilarkesto.imdb;

import ilarkesto.core.base.Str;

public class ImdbRecord {

	private String id;
	private String title;
	private String titleDe;
	private Integer year;
	private String coverId;
	private String trailerId;

	public ImdbRecord(String id, String title, String titleDe, Integer year, String coverId, String trailerId) {
		super();
		this.id = id;
		this.title = title;
		this.titleDe = titleDe;
		this.year = year;
		this.coverId = coverId;
		this.trailerId = trailerId;
	}

	public String getId() {
		return id;
	}

	public String getTrailerId() {
		return trailerId;
	}

	public String getTitle() {
		return title;
	}

	public boolean isTitleSet() {
		return !Str.isBlank(title);
	}

	public String getTitleDe() {
		return titleDe;
	}

	public boolean isTitleDeSet() {
		return !Str.isBlank(titleDe);
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
		return title + " (" + year + ")";
	}

}
