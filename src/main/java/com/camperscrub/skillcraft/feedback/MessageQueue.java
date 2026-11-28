package com.camperscrub.skillcraft.feedback;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * Shows messages one at a time, each for at least {@code displayTicks}. Pure logic: no Minecraft
 * classes.
 *
 * <p>The action bar can only show one message, and a new one replaces the old straight away. At
 * level 100 a player earns a skill point and Super Breaker improves in the same moment, so without
 * this queue the second message would hide the first before anyone could read it.
 */
public final class MessageQueue<T> {

    private final long displayTicks;
    private final Deque<T> waiting = new ArrayDeque<>();
    private long busyUntil;

    public MessageQueue(long displayTicks) {
        this.displayTicks = displayTicks;
    }

    /**
     * A new message at tick {@code now}.
     *
     * <p>EXERCISE 1. If nothing is waiting and the last message has had its time ({@code now} is
     * at or after {@code busyUntil}), it can show straight away: set {@code busyUntil} to
     * {@code now + displayTicks} and return it. Otherwise add it to the back of {@code waiting}
     * and return empty.
     */
    public Optional<T> offer(T message, long now) {
        if (waiting.isEmpty() && now >= busyUntil) {
            busyUntil = now + displayTicks;
            return Optional.of(message);
        }
        waiting.add(message);
        return Optional.empty();
    }

    /**
     * Called every tick: the next waiting message if it's its turn, else empty.
     *
     * <p>EXERCISE 2. If something is waiting and {@code now} is at or after {@code busyUntil},
     * take it from the front, set {@code busyUntil} as above and return it.
     */
    public Optional<T> poll(long now) {
        if (waiting.isEmpty() || now < busyUntil) {
            return Optional.empty();
        }
        busyUntil = now + displayTicks;
        return Optional.of(waiting.removeFirst());
    }
}