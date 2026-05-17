package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        correct.addLast(5);
        correct.addLast(10);
        correct.addLast(15);

        broken.addLast(5);
        broken.addLast(10);
        broken.addLast(15);

        assertEquals(correct.size(), broken.size());

        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                buggy.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = correct.size();
                int s2 = buggy.size();

                if (size == 0) assertEquals(0, s2);
                else assertNotEquals(0, s2);
            } else if (operationNumber == 2) {
                // getLast

                int size = correct.size();
                if (size == 0) {
                    continue;
                }

                int s2 = buggy.size();
                assertNotEquals(0, s2);

                int last = correct.getLast();
                int last2 = buggy.getLast();

                assertEquals(last, last2);
            } else if (operationNumber == 3) {
                // removeLast

                int size = correct.size();
                if (size == 0) {
                    continue;
                }

                int last = correct.removeLast();
                int last2 = buggy.removeLast();
                assertEquals(last, last2);
            }
        }
    }
}
