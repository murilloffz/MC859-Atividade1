package problems.qbf.solvers;

import java.io.IOException;
import java.util.*;

import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.QBF_Inverse;
import solutions.Solution;



/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * ). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 *
 * @author ccavellucci, fusberti
 */
public class GRASP_RPG extends AbstractGRASP<Integer> {

    /**
     * Constructor for the GRASP_QBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param alpha
     *            The GRASP greediness-randomness parameter (within the range
     *            [0,1])
     * @param time
     *            The number of iterations which the GRASP will be executed.
     * @param filename
     *            Name of the file for which the objective function parameters
     *            should be read.
     * @throws IOException
     *             necessary for I/O operations.
     */
    private final Integer randomSteps;

    public GRASP_RPG(Double alpha, Integer time, String filename, Integer randomSteps) throws IOException {
        super(new QBF_Inverse(filename), alpha, time);
        this.randomSteps = randomSteps;
    }

    public Solution<Integer> constructiveHeuristic() {
        CL = makeCL();
        RCL = makeRCL();
        sol = createEmptySol();
        cost = Double.POSITIVE_INFINITY;
        int currentStep = 0;

        /* Main loop, which repeats until the stopping criteria is reached. */
        while (!constructiveStopCriteria()) {

            double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
            cost = ObjFunction.evaluate(sol);
            updateCL();

            /*
             * Explore all candidate elements to enter the solution, saving the
             * highest and lowest cost variation achieved by the candidates.
             */
            for (Integer c : CL) {
                Double deltaCost = ObjFunction.evaluateInsertionCost(c, sol);
                if (deltaCost < minCost)
                    minCost = deltaCost;
                if (deltaCost > maxCost)
                    maxCost = deltaCost;
            }

            /*
             * Add all CL elements to RCL and choose one randomly if on a random step
             * Else it takes the pure greedy approach
             */
            if (currentStep < (int) Math.floor(ObjFunction.getDomainSize()*0.1)){
                RCL.addAll(CL);
                if(RCL.size() > 0) {
                    int rndIndex = rng.nextInt(RCL.size());
                    Integer inCand = RCL.get(rndIndex);
                    CL.remove(inCand);
                    sol.add(inCand);
                    ObjFunction.evaluate(sol);
                    RCL.clear();
                }
            }
            else{
                for (Integer c : CL) {
                    Double deltaCost = ObjFunction.evaluateInsertionCost(c, sol);
                    if (deltaCost <= minCost) {
                        RCL.add(c);
                    }
                }
                if(RCL.size() > 0) {
                    int rndIndex = rng.nextInt(RCL.size());
                    Integer inCand = RCL.get(rndIndex);
                    CL.remove(inCand);
                    sol.add(inCand);
                    ObjFunction.evaluate(sol);
                    RCL.clear();
                }
            }
            currentStep++;

            /* Choose a candidate randomly from the RCL */
            if(RCL.size() > 0) {
                int rndIndex = rng.nextInt(RCL.size());
                Integer inCand = RCL.get(rndIndex);
                CL.remove(inCand);
                sol.add(inCand);
                ObjFunction.evaluate(sol);
                RCL.clear();
            }
        }

        return sol;
    }

    /*
     * (non-Javadoc)
     *
     * @see grasp.abstracts.AbstractGRASP#makeCL()
     */
    @Override
    public ArrayList<Integer> makeCL() {

        ArrayList<Integer> _CL = new ArrayList<Integer>();
        for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
            Integer cand = i;
            _CL.add(cand);
        }

        return _CL;

    }

    /*
     * (non-Javadoc)
     *
     * @see grasp.abstracts.AbstractGRASP#makeRCL()
     */
    @Override
    public ArrayList<Integer> makeRCL() {

        ArrayList<Integer> _RCL = new ArrayList<Integer>();

        return _RCL;

    }

    /*
     * (non-Javadoc)
     *
     * @see grasp.abstracts.AbstractGRASP#updateCL()
     */
    @Override
    public void updateCL() {
        ObjFunction.getDomainSize();
        int[] pesos = ObjFunction.getWeights();
        int pesoAtual = ObjFunction.getCurrentWeight();
        int maxPeso = ObjFunction.getMaxWeight();

        Iterator<Integer> iterator = CL.iterator();
        while (iterator.hasNext()) {
            Integer valor = iterator.next();
            if (pesos[valor] + pesoAtual > maxPeso) {
                iterator.remove();
            }
        }

        // do nothing since all elements off the solution are viable candidates.

    }

    /**
     * {@inheritDoc}
     *
     * This createEmptySol instantiates an empty solution and it attributes a
     * zero cost, since it is known that a QBF solution with all variables set
     * to zero has also zero cost.
     */
    @Override
    public Solution<Integer> createEmptySol() {
        Solution<Integer> sol = new Solution<Integer>();
        sol.cost = 0.0;
        return sol;
    }

    /**
     * {@inheritDoc}
     *
     * The local search operator developed for the QBF objective function is
     * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
     */
    @Override
    public Solution<Integer> localSearch() {
        int[] pesos = ObjFunction.getWeights();
        int pesoAtual = ObjFunction.getCurrentWeight();
        int maxPeso = ObjFunction.getMaxWeight();

        var lasVariables = ObjFunction.getVariables();

        ArrayList<Integer> candidatosDisps = new ArrayList<Integer>();

        for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
            if(lasVariables[i] == 0.0)
                candidatosDisps.add(i);
        }


        Double minDeltaCost = Double.POSITIVE_INFINITY;
        Integer bestCandIn = null, bestCandOut = null;


        // Evaluate removals
        for (Integer candOut : sol) {
            double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
            if (deltaCost < minDeltaCost) {
                minDeltaCost = deltaCost;
                bestCandIn = null;
                bestCandOut = candOut;
            }
        }

        for (Integer candOut : sol) {
            int pesoSaida = pesos[candOut];
            int pesoMaxPossivel = maxPeso - pesoAtual - pesoSaida;
            for(Integer candIn : candidatosDisps) {
                if(pesos[candIn] <= pesoMaxPossivel) {
                    double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
                    if (deltaCost < minDeltaCost) {
                        minDeltaCost = deltaCost;
                        bestCandIn = candIn;
                        bestCandOut = candOut;
                    }
                }
            }
        }

        // Implement the best move, if it reduces the solution cost.
        if (minDeltaCost < -Double.MIN_VALUE) {
            if (bestCandOut != null) {
                //System.out.println("Estou tirando o "+bestCandOut);
                sol.remove(bestCandOut);
                CL.add(bestCandOut);
            }
            if (bestCandIn != null) {
                //System.out.println("Estou inserindo o "+bestCandIn);
                sol.add(bestCandIn);
                CL.remove(bestCandIn);
            }
            ObjFunction.evaluate(sol);
        }
        return null;
    }

    //LOCALSEARCH FIRSTIMPROVE
	/*@Override
	public Solution<Integer> localSearch() {
		int[] pesos = ObjFunction.getWeights();
		int pesoAtual = ObjFunction.getCurrentWeight();
		int maxPeso = ObjFunction.getMaxWeight();

		var lasVariables = ObjFunction.getVariables();

		ArrayList<Integer> candidatosDisps = new ArrayList<Integer>();

		for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
			if(lasVariables[i] == 0.0)
				candidatosDisps.add(i);
		}


		Double minDeltaCost = Double.POSITIVE_INFINITY;
		Integer bestCandIn = null, bestCandOut = null;


		for (Integer candOut : sol) {
			int pesoSaida = pesos[candOut];
			int pesoMaxPossivel = maxPeso - pesoAtual - pesoSaida;
			for(int candIn : candidatosDisps) {
				if(pesos[candIn] <= pesoMaxPossivel) {
					double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
					if (deltaCost < minDeltaCost) {
						sol.add(bestCandIn);
						CL.add(bestCandOut);
						sol.remove(bestCandOut);
						CL.remove(bestCandIn);
						ObjFunction.evaluate(sol);
						return null;
					}
				}
			}
		}

		// Evaluate removals
		for (Integer candOut : sol) {
			double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
			if (deltaCost < minDeltaCost) {
				sol.remove(bestCandOut);
				CL.add(bestCandOut);
				ObjFunction.evaluate(sol);
				return null;
			}
		}

		return null;
	}*/





    /**
     * A main method used for testing the GRASP metaheuristic.
     *
     */
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GRASP_RPG grasp = new GRASP_RPG(0.2, 3600000, "instances/kqbf/kqbf020", 2);
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

    }

}

