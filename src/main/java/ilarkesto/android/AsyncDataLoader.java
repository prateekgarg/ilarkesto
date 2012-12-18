package ilarkesto.android;

import ilarkesto.core.logging.Log;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class AsyncDataLoader<C> {

	private LoaderTask task;
	private List<OnDataLoadedListener<C>> listeners = new ArrayList<OnDataLoadedListener<C>>();

	private DataProvider<C> dataProvider;

	public AsyncDataLoader(DataProvider<C> dataProvider) {
		super();
		this.dataProvider = dataProvider;
	}

	public final void load(OnDataLoadedListener<C> callback) {
		synchronized (listeners) {
			listeners.add(callback);
			if (task == null) startTask();
		}
	}

	private void onDataLoaded(C data) {
		Log.TEST("onDataLoaded", data);
		synchronized (listeners) {
			task = null;
			for (OnDataLoadedListener<C> listener : listeners) {
				listener.onDataLoaded(data);
			}
			listeners.clear();
		}
	}

	private void startTask() {
		task = new LoaderTask();
		task.doInBackground();
	}

	private class LoaderTask extends AsyncTask<Void, Void, C> {

		@Override
		protected C doInBackground(Void... arg0) {
			Log.TEST("doInBackground");
			C data = dataProvider.getData();
			Log.TEST("doInBackground finished:", data);
			return data;
		}

		@Override
		protected void onPostExecute(C data) {
			onDataLoaded(data);
		}

	}

	public static interface OnDataLoadedListener<C> {

		void onDataLoaded(C data);

	}

}
