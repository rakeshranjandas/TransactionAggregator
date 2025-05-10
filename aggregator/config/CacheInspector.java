package aggregator.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheInspector {

    @Autowired
    private CacheManager cacheManager;

    public void printCacheContents(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            System.out.println("Cache not found: " + cacheName);
            return;
        }

        // NOTE: This only works for ConcurrentMapCache (default)
        Object nativeCache = cache.getNativeCache();
        if (nativeCache instanceof java.util.concurrent.ConcurrentMap<?, ?> map) {
            map.forEach((key, value) -> System.out.println(key + " => " + value));
        } else {
            System.out.println("Unsupported cache type: " + nativeCache.getClass());
        }
    }
}
