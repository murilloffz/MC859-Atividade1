package problems.tests.randompgreedy;

import problems.qbf.solvers.GRASP_RPG;
import solutions.Solution;

import java.io.IOException;

public class GRASP_RPGTest3 {
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GRASP_RPG grasp = new GRASP_RPG(0.2, 1800000, "instances/kqbf/kqbf060",2);
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

    }
}
