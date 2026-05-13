package org.apache.commons.lang3.concurrent;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.concurrent.AbstractCircuitBreaker;

/* JADX INFO: loaded from: classes.dex */
public class EventCountCircuitBreaker extends AbstractCircuitBreaker<Integer> {
    private static final Map<AbstractCircuitBreaker.State, StateStrategy> STRATEGY_MAP = createStrategyMap();
    private final AtomicReference<CheckIntervalData> checkIntervalData;
    private final long closingInterval;
    private final int closingThreshold;
    private final long openingInterval;
    private final int openingThreshold;

    public EventCountCircuitBreaker(int openingThreshold, long openingInterval, TimeUnit openingUnit, int closingThreshold, long closingInterval, TimeUnit closingUnit) {
        this.checkIntervalData = new AtomicReference<>(new CheckIntervalData(0, 0L));
        this.openingThreshold = openingThreshold;
        this.openingInterval = openingUnit.toNanos(openingInterval);
        this.closingThreshold = closingThreshold;
        this.closingInterval = closingUnit.toNanos(closingInterval);
    }

    public EventCountCircuitBreaker(int openingThreshold, long checkInterval, TimeUnit checkUnit, int closingThreshold) {
        this(openingThreshold, checkInterval, checkUnit, closingThreshold, checkInterval, checkUnit);
    }

    public EventCountCircuitBreaker(int threshold, long checkInterval, TimeUnit checkUnit) {
        this(threshold, checkInterval, checkUnit, threshold);
    }

    public int getOpeningThreshold() {
        return this.openingThreshold;
    }

    public long getOpeningInterval() {
        return this.openingInterval;
    }

    public int getClosingThreshold() {
        return this.closingThreshold;
    }

    public long getClosingInterval() {
        return this.closingInterval;
    }

    @Override // org.apache.commons.lang3.concurrent.AbstractCircuitBreaker, org.apache.commons.lang3.concurrent.CircuitBreaker
    public boolean checkState() {
        return performStateCheck(0);
    }

    @Override // org.apache.commons.lang3.concurrent.AbstractCircuitBreaker, org.apache.commons.lang3.concurrent.CircuitBreaker
    public boolean incrementAndCheckState(Integer increment) {
        return performStateCheck(increment.intValue());
    }

    public boolean incrementAndCheckState() {
        return incrementAndCheckState((Integer) 1);
    }

    @Override // org.apache.commons.lang3.concurrent.AbstractCircuitBreaker, org.apache.commons.lang3.concurrent.CircuitBreaker
    public void open() {
        super.open();
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    @Override // org.apache.commons.lang3.concurrent.AbstractCircuitBreaker, org.apache.commons.lang3.concurrent.CircuitBreaker
    public void close() {
        super.close();
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    private boolean performStateCheck(int increment) {
        AbstractCircuitBreaker.State currentState;
        CheckIntervalData currentData;
        CheckIntervalData nextData;
        do {
            long time = now();
            currentState = this.state.get();
            currentData = this.checkIntervalData.get();
            nextData = nextCheckIntervalData(increment, currentData, currentState, time);
        } while (!updateCheckIntervalData(currentData, nextData));
        if (stateStrategy(currentState).isStateTransition(this, currentData, nextData)) {
            currentState = currentState.oppositeState();
            changeStateAndStartNewCheckInterval(currentState);
        }
        return !isOpen(currentState);
    }

    private boolean updateCheckIntervalData(CheckIntervalData currentData, CheckIntervalData nextData) {
        return currentData == nextData || this.checkIntervalData.compareAndSet(currentData, nextData);
    }

    private void changeStateAndStartNewCheckInterval(AbstractCircuitBreaker.State newState) {
        changeState(newState);
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    private CheckIntervalData nextCheckIntervalData(int increment, CheckIntervalData currentData, AbstractCircuitBreaker.State currentState, long time) {
        if (stateStrategy(currentState).isCheckIntervalFinished(this, currentData, time)) {
            CheckIntervalData nextData = new CheckIntervalData(increment, time);
            return nextData;
        }
        CheckIntervalData nextData2 = currentData.increment(increment);
        return nextData2;
    }

    long now() {
        return System.nanoTime();
    }

    private static StateStrategy stateStrategy(AbstractCircuitBreaker.State state) {
        return STRATEGY_MAP.get(state);
    }

    private static Map<AbstractCircuitBreaker.State, StateStrategy> createStrategyMap() {
        Map<AbstractCircuitBreaker.State, StateStrategy> map = new EnumMap<>(AbstractCircuitBreaker.State.class);
        map.put(AbstractCircuitBreaker.State.CLOSED, new StateStrategyClosed());
        map.put(AbstractCircuitBreaker.State.OPEN, new StateStrategyOpen());
        return map;
    }

    private static class CheckIntervalData {
        private final long checkIntervalStart;
        private final int eventCount;

        CheckIntervalData(int count, long intervalStart) {
            this.eventCount = count;
            this.checkIntervalStart = intervalStart;
        }

        public int getEventCount() {
            return this.eventCount;
        }

        public long getCheckIntervalStart() {
            return this.checkIntervalStart;
        }

        public CheckIntervalData increment(int delta) {
            return delta == 0 ? this : new CheckIntervalData(getEventCount() + delta, getCheckIntervalStart());
        }
    }

    private static abstract class StateStrategy {
        protected abstract long fetchCheckInterval(EventCountCircuitBreaker eventCountCircuitBreaker);

        public abstract boolean isStateTransition(EventCountCircuitBreaker eventCountCircuitBreaker, CheckIntervalData checkIntervalData, CheckIntervalData checkIntervalData2);

        private StateStrategy() {
        }

        public boolean isCheckIntervalFinished(EventCountCircuitBreaker breaker, CheckIntervalData currentData, long now) {
            return now - currentData.getCheckIntervalStart() > fetchCheckInterval(breaker);
        }
    }

    private static class StateStrategyClosed extends StateStrategy {
        private StateStrategyClosed() {
            super();
        }

        @Override // org.apache.commons.lang3.concurrent.EventCountCircuitBreaker.StateStrategy
        public boolean isStateTransition(EventCountCircuitBreaker breaker, CheckIntervalData currentData, CheckIntervalData nextData) {
            return nextData.getEventCount() > breaker.getOpeningThreshold();
        }

        @Override // org.apache.commons.lang3.concurrent.EventCountCircuitBreaker.StateStrategy
        protected long fetchCheckInterval(EventCountCircuitBreaker breaker) {
            return breaker.getOpeningInterval();
        }
    }

    private static class StateStrategyOpen extends StateStrategy {
        private StateStrategyOpen() {
            super();
        }

        @Override // org.apache.commons.lang3.concurrent.EventCountCircuitBreaker.StateStrategy
        public boolean isStateTransition(EventCountCircuitBreaker breaker, CheckIntervalData currentData, CheckIntervalData nextData) {
            return nextData.getCheckIntervalStart() != currentData.getCheckIntervalStart() && currentData.getEventCount() < breaker.getClosingThreshold();
        }

        @Override // org.apache.commons.lang3.concurrent.EventCountCircuitBreaker.StateStrategy
        protected long fetchCheckInterval(EventCountCircuitBreaker breaker) {
            return breaker.getClosingInterval();
        }
    }
}
