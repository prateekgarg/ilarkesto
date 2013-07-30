package ilarkesto.law;

import ilarkesto.core.base.OperationObserver;
import ilarkesto.json.ARemoteJsonCache;

import java.io.File;

public class BookIndexCache extends ARemoteJsonCache<BookIndex> {

	private ALawProvider lawProvider;

	BookIndexCache(File file, ALawProvider lawProvider) {
		super(BookIndex.class, file);
		this.lawProvider = lawProvider;
	}

	@Override
	protected BookIndex createInitialPayload() {
		BookIndex payload = lawProvider.loadPrepackagedBookIndex();
		log.info("Prepackaged book index:", payload);
		return payload;
	}

	@Override
	protected BookIndex onUpdate(BookIndex index, boolean forced, boolean invalidated, OperationObserver observer) {
		if (!forced && !invalidated) {
			if (index != null && !index.getBooks().isEmpty()) {
				if (getDaysSinceLastUpdated() < 90) return null;
			}
		}
		observer.onOperationInfoChanged(OperationObserver.DOWNLOADING, lawProvider.getSourceUrl());
		return lawProvider.loadBookIndex();
	}

}
