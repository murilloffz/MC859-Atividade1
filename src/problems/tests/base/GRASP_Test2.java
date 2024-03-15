package problems.tests.base;

import problems.qbf.solvers.GRASP_QBF;
import solutions.Solution;

import java.io.IOException;

public class GRASP_Test2 {
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GRASP_QBF grasp = new GRASP_QBF(0.1, 1800000, "instances/kqbf/kqbf040");
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

    }
}
