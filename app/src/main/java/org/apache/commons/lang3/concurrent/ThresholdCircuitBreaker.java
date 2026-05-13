package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes.dex */
public class ThresholdCircuitBreaker extends AbstractCircuitBreaker<Long> {
    private static final long INITIAL_COUNT = 0;
    private final long threshold;
    private final AtomicLong used = new AtomicLong(0);

    public ThresholdCircuitBreaker(long threshold) {
        this.threshold = threshold;
    }

    public long getThreshold() {
        return this.threshold;
    }

    @Override // org.apache.commons.lang3.concurrent.AbstractCircuitBreaker, org.apache.commons.lang3.concurrent.CircuitBreaker
    public boolean checkState() {
        return isOpen();
    }

    @Override // org.apache.commons.lang3.concurrent.AbstractCircuitBreaker, org.apache.commons.lang3.concurrent.CircuitBreaker
    public void close() {
        super.close();
        this.used.set(0L);
    }

    @Override // org.apache.commons.lang3.concurrent.AbstractCircuitBreaker, org.apache.commons.lang3.concurrent.CircuitBreaker
    public boolean incrementAndCheckState(Long increment) {
        if (this.threshold == 0) {
            open();
        }
        long used = this.used.addAndGet(increment.longValue());
        if (used > this.threshold) {
            open();
        }
        return checkState();
    }
}
