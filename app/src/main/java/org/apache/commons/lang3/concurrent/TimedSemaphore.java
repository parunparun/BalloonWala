package org.apache.commons.lang3.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

/* JADX INFO: loaded from: classes.dex */
public class TimedSemaphore {
    public static final int NO_LIMIT = 0;
    private static final int THREAD_POOL_SIZE = 1;
    private int acquireCount;
    private final ScheduledExecutorService executorService;
    private int lastCallsPerPeriod;
    private int limit;
    private final boolean ownExecutor;
    private final long period;
    private long periodCount;
    private boolean shutdown;
    private ScheduledFuture<?> task;
    private long totalAcquireCount;
    private final TimeUnit unit;

    public TimedSemaphore(long timePeriod, TimeUnit timeUnit, int limit) {
        this(null, timePeriod, timeUnit, limit);
    }

    public TimedSemaphore(ScheduledExecutorService service, long timePeriod, TimeUnit timeUnit, int limit) {
        Validate.inclusiveBetween(1L, Long.MAX_VALUE, timePeriod, "Time period must be greater than 0!");
        this.period = timePeriod;
        this.unit = timeUnit;
        if (service != null) {
            this.executorService = service;
            this.ownExecutor = false;
        } else {
            ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
            s.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            s.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            this.executorService = s;
            this.ownExecutor = true;
        }
        setLimit(limit);
    }

    public final synchronized int getLimit() {
        return this.limit;
    }

    public final synchronized void setLimit(int limit) {
        this.limit = limit;
    }

    public synchronized void shutdown() {
        if (!this.shutdown) {
            if (this.ownExecutor) {
                getExecutorService().shutdownNow();
            }
            if (this.task != null) {
                this.task.cancel(false);
            }
            this.shutdown = true;
        }
    }

    public synchronized boolean isShutdown() {
        return this.shutdown;
    }

    public synchronized void acquire() throws InterruptedException {
        boolean canPass;
        prepareAcquire();
        do {
            canPass = acquirePermit();
            if (!canPass) {
                wait();
            }
        } while (!canPass);
    }

    public synchronized boolean tryAcquire() {
        prepareAcquire();
        return acquirePermit();
    }

    public synchronized int getLastAcquiresPerPeriod() {
        return this.lastCallsPerPeriod;
    }

    public synchronized int getAcquireCount() {
        return this.acquireCount;
    }

    public synchronized int getAvailablePermits() {
        return getLimit() - getAcquireCount();
    }

    public synchronized double getAverageCallsPerPeriod() {
        double d;
        if (this.periodCount == 0) {
            d = 0.0d;
        } else {
            d = this.totalAcquireCount / this.periodCount;
        }
        return d;
    }

    public long getPeriod() {
        return this.period;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    protected ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }

    protected ScheduledFuture<?> startTimer() {
        return getExecutorService().scheduleAtFixedRate(new Runnable() { // from class: org.apache.commons.lang3.concurrent.-$$Lambda$TimedSemaphore$BerjNo-7OmIeHNeVloA1bHiT_qo
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startTimer$0$TimedSemaphore();
            }
        }, getPeriod(), getPeriod(), getUnit());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: renamed from: endOfPeriod, reason: merged with bridge method [inline-methods] */
    public synchronized void lambda$startTimer$0$TimedSemaphore() {
        this.lastCallsPerPeriod = this.acquireCount;
        this.totalAcquireCount += (long) this.acquireCount;
        this.periodCount++;
        this.acquireCount = 0;
        notifyAll();
    }

    private void prepareAcquire() {
        if (isShutdown()) {
            throw new IllegalStateException("TimedSemaphore is shut down!");
        }
        if (this.task == null) {
            this.task = startTimer();
        }
    }

    private boolean acquirePermit() {
        if (getLimit() <= 0 || this.acquireCount < getLimit()) {
            this.acquireCount++;
            return true;
        }
        return false;
    }
}
