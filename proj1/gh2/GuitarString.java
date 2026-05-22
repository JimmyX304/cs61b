package gh2;

import deque.ArrayDeque;
import deque.Deque;

//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor
    private int size;

    /* Buffer for storing sound data. */
    private Deque<Double> buffer = new ArrayDeque<>();

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        size = Math.round((int) (SR / frequency));
        for (int i = 0; i < size; i++) {
            buffer.addLast(0.0);
        }
    }

    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        for (int i = 0; i < size; i++) {
            buffer.removeFirst();
            double toAdd = Math.random() - 0.5;
            buffer.addLast(toAdd);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double d1 = buffer.removeFirst();
        double d2 = sample();
        buffer.addLast(0.5 * DECAY * (d1 + d2));
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }
}
