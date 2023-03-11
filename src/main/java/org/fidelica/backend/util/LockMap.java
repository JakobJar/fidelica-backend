package org.fidelica.backend.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockMap<K> {

    private final Map<K, ReentrantLock> locks;

    public LockMap() {
        this.locks = new ConcurrentHashMap<>();
    }

    protected ReentrantLock getLock(K key) {
        return locks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    public void lock(K key) {
        getLock(key).lock();
    }

    public boolean tryLock(K key) {
        return getLock(key).tryLock();
    }

    public void unlock(K key) {
        locks.computeIfPresent(key, (k, lock) -> {
            lock.unlock();
            if (lock.isLocked())
                return lock;
            return null;
        });
    }
}
