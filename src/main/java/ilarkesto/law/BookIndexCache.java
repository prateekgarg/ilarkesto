package ilarkesto.law;

import ilarkesto.json.ARemoteJsonCache;

import java.io.File;

public class BookIndexCache extends ARemoteJsonCache<BookIndex> {

	private ALawProvider lawProvider;

	public BookIndexCache(File file, ALawProvider lawProvider) {
		super(file);
		this.lawProvider = lawProvider;
	}

	@Override
	protected Class<BookIndex> getPayloadType() {
		return BookIndex.class;
	}

	@Override
	protected BookIndex onUpdate(BookIndex index, boolean forced) {
		if (!forced) {
			if (index != null && !index.getBooks().isEmpty()) {
				if (getDaysSinceLastUpdated() < 30) return null;
			}
		}
		return lawProvider.loadBookIndex();
	}

}
