package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;


public class ArrayDequeTest {

    @Test
    public void largestTest() {
        ArrayDeque<Integer> buggy = new ArrayDeque<>();
        LinkedListDeque<Integer> correct = new LinkedListDeque<>();

        int N = 5000000;
        for (int i = 0; i < N; i++) {

            int operationNumber = StdRandom.uniform(0, 6);

            if (operationNumber == 0) {
                // addFirst

                int randVal = StdRandom.uniform(0, 100);
                correct.addFirst(randVal);
                buggy.addFirst(randVal);
            } else if (operationNumber == 1) {
                // addLast

                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                buggy.addLast(randVal);
            } else if (operationNumber == 2) {
                // isEmpty

                boolean correctAnswer = correct.isEmpty();
                boolean buggyAnswer = buggy.isEmpty();

                assertEquals(correctAnswer, buggyAnswer);
            } else if (operationNumber == 3) {
                // size

                int correctAnswer = correct.size();
                int buggyAnswer = buggy.size();

                assertEquals(correctAnswer, buggyAnswer);
            } else if (operationNumber == 4) {
                // removeFirst

                if (correct.size() == 0) {
                    continue;
                }

                int correctAnswer = correct.removeFirst();
                int buggyAnswer = buggy.removeFirst();

                assertEquals(correctAnswer, buggyAnswer);
            } else if (operationNumber == 5) {
                // removeLast

                if (correct.size() == 0) {
                    continue;
                }

                int correctAnswer = correct.removeLast();
                int buggyAnswer = buggy.removeLast();

                assertEquals(correctAnswer, buggyAnswer);
            }
        }
    }
}
