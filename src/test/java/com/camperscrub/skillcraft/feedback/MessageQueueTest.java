package com.camperscrub.skillcraft.feedback;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MessageQueueTest {

    private final MessageQueue<String> queue = new MessageQueue<>(40);

    @Test
    void theFirstMessageShowsStraightAway() {
        assertEquals(Optional.of("level"), queue.offer("level", 100));
    }

    @Test
    void aSecondMessageWaitsItsTurn() {
        queue.offer("level", 100);
        assertEquals(Optional.empty(), queue.offer("improved", 100));
        assertEquals(Optional.empty(), queue.poll(139));
        assertEquals(Optional.of("improved"), queue.poll(140));
    }

    @Test
    void waitingMessagesComeOutInOrder() {
        queue.offer("first", 100);
        queue.offer("second", 100);
        queue.offer("third", 100);
        assertEquals(Optional.of("second"), queue.poll(140));
        assertEquals(Optional.empty(), queue.poll(141));
        assertEquals(Optional.of("third"), queue.poll(180));
        assertEquals(Optional.empty(), queue.poll(500));
    }

    @Test
    void aMessageAfterAQuietSpellShowsStraightAway() {
        queue.offer("first", 100);
        assertEquals(Optional.of("later"), queue.offer("later", 200));
    }

    @Test
    void aNewMessageCannotJumpTheQueue() {
        queue.offer("first", 100);
        queue.offer("second", 100);
        // The first has had its time, but "second" is still waiting, so "third" goes behind it.
        assertEquals(Optional.empty(), queue.offer("third", 150));
        assertEquals(Optional.of("second"), queue.poll(150));
        assertEquals(Optional.of("third"), queue.poll(190));
    }

    @Test
    void pollingAnEmptyQueueGivesNothing() {
        assertEquals(Optional.empty(), queue.poll(100));
    }
}
