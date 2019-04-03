package engine.core;

import engine.utils.Pair;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class EventQueue<T> {

    private Queue<Pair<Long, T>> events;

    public EventQueue() {
        events = new PriorityQueue<>(new PairComparator());
    }

    public void enqueueEvent(T event, int millis) {
        Long time = System.nanoTime() + millis * (long) 10e5;
        events.add(new Pair<>(time, event));
    }

    public T tick() {
        if (!events.isEmpty()) {
            if (System.nanoTime() >= events.peek().getLeft()) {
                return events.remove().getRight();
            }
        }
        return null;
    }

    private class PairComparator implements Comparator<Pair<Long, T>> {

        @Override
        public Comparator<Pair<Long, T>> reversed() {
            return Comparator.super.reversed();
        }

        @Override
        public int compare(Pair<Long, T> o1, Pair<Long, T> o2) {
            return (int) (((double) o1.getLeft() * 10.0e-9) - ((double) o2.getLeft() * 10e-9));
        }

    }
}
