package problems.tests.pop;

import problems.qbf.solvers.GRASP_POP;
import solutions.Solution;

import java.io.IOException;

public class GRASP_POPTest7 {
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GRASP_POP grasp = new GRASP_POP(0.1, 1800000, "instances/kqbf/kqbf400");
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

    }
}
