package problems.tests.first;

import problems.qbf.solvers.GRASP_FirstImproving;
import solutions.Solution;

import java.io.IOException;

public class GRASP_FirstImprovingTest5 {
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GRASP_FirstImproving grasp = new GRASP_FirstImproving(0.1, 1800000, "instances/kqbf/kqbf100");
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

    }
}
