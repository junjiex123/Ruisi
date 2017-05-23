package me.yluo.ruisiapp.myhttp;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpClient extends SyncHttpClient {
    private final ExecutorService threadPool;

    public AsyncHttpClient() {
        super();
        threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void get(final String url, final ResponseHandler handler) {
        threadPool.execute(() -> request(url, Method.GET, null, handler));
    }

    @Override
    public void post(final String url, final Map<String, String> map, final ResponseHandler handler) {
        threadPool.execute(() -> request(url, Method.POST, map, handler));
    }

    @Override
    public void head(final String url, final Map<String, String> map, final ResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                request(url, Method.HEAD, map, handler);
            }
        });
    }
}
