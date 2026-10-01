package com.fashionsense.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ProductCacheService {

    private static final String PRODUCT_DETAILS_CACHE =
            "productDetails";

    private final CacheManager cacheManager;

    public ProductCacheService(
            CacheManager cacheManager
    ) {
        this.cacheManager = cacheManager;
    }

    public void evictProductDetailAfterCommit(
            String slug
    ) {

        if (slug == null || slug.isBlank()) {
            return;
        }

        if (TransactionSynchronizationManager
                .isSynchronizationActive()) {

            TransactionSynchronizationManager
                    .registerSynchronization(
                            new TransactionSynchronization() {

                                @Override
                                public void afterCommit() {
                                    evictProductDetail(slug);
                                }
                            }
                    );

            return;
        }

        evictProductDetail(slug);
    }

    private void evictProductDetail(
            String slug
    ) {

        Cache cache =
                cacheManager.getCache(
                        PRODUCT_DETAILS_CACHE
                );

        if (cache != null) {
            cache.evict(slug);
        }
    }
}