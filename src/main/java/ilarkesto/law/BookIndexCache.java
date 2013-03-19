package ilarkesto.law;

import ilarkesto.json.ARemoteJsonCache;

import java.io.File;

public class BookIndexCache extends ARemoteJsonCache<BookIndex> {

	private ALawProvider lawProvider;

	BookIndexCache(File file, ALawProvider lawProvider) {
		super(BookIndex.class, file);
		this.lawProvider = lawProvider;
	}

	@Override
	protected BookIndex onUpdate(BookIndex index, boolean forced) {
		if (!forced) {
			if (index != null && !index.getBooks().isEmpty()) {
				if (getDaysSinceLastUpdated() < 90) return null;
			}
		}
		return lawProvider.loadBookIndex();
	}

}
