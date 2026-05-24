package tester;

import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void largestTest() {
        StudentArrayDeque<Integer> buggy = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> correct = new ArrayDequeSolution<>();

        StringBuilder res = new StringBuilder();

        int N = 5000000;
        for (int i = 0; i < N; i++) {

            int operationNumber = StdRandom.uniform(0, 7);

            if (operationNumber == 0) {
                // addFirst

                int randVal = StdRandom.uniform(0, 100);
                correct.addFirst(randVal);
                buggy.addFirst(randVal);

                res.append("addFirst(" + randVal + ")\n");

            } else if (operationNumber == 1) {
                // addLast

                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                buggy.addLast(randVal);

                res.append("addLast(" + randVal + ")\n");

            } else if (operationNumber == 2) {
                // isEmpty

                boolean correctAnswer = correct.isEmpty();
                boolean buggyAnswer = buggy.isEmpty();

                res.append("isEmpty()\n");

                assertEquals(res.toString(), correctAnswer, buggyAnswer);
            } else if (operationNumber == 3) {
                // size

                int correctAnswer = correct.size();
                int buggyAnswer = buggy.size();

                res.append("size()\n");

                assertEquals(res.toString(), correctAnswer, buggyAnswer);
            } else if (operationNumber == 4) {
                // removeFirst

                if (correct.size() == 0) {
                    continue;
                }

                int correctAnswer = correct.removeFirst();
                int buggyAnswer = buggy.removeFirst();

                res.append("removeFirst()\n");

                assertEquals(res.toString(), correctAnswer, buggyAnswer);
            } else if (operationNumber == 5) {
                // removeLast

                if (correct.size() == 0) {
                    continue;
                }

                int correctAnswer = correct.removeLast();
                int buggyAnswer = buggy.removeLast();

                res.append("removeLast()\n");

                assertEquals(res.toString(), correctAnswer, buggyAnswer);
            } else if (operationNumber == 6) {
                if (correct.isEmpty()) {
                    assertEquals(res.toString(), buggy.isEmpty(), true);
                } else {
                    assertEquals(res.toString(), correct.size(), buggy.size());

                    int rand = StdRandom.uniform(0, correct.size());

                    res.append("get(" + rand + ")\n");

                    assertEquals(res.toString(), correct.get(rand), buggy.get(rand));
                }
            }
        }
    }
}
