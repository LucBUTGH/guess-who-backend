package projets.guesswho.backend.service

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

@Component
class LoginRateLimiter {
    private val lock = ReentrantLock()
    private val attempts = ConcurrentHashMap<String, MutableList<Long>>()
    private val windowMs = 60_000L
    private val maxAttempts = 10

    fun isAllowed(key: String): Boolean {
        lock.lock()
        try {
            val now = System.currentTimeMillis()
            val times = attempts.getOrPut(key) { mutableListOf() }
            times.removeIf { now - it > windowMs }
            if (times.size >= maxAttempts) return false
            times.add(now)
            return true
        } finally {
            lock.unlock()
        }
    }
}
