package me.nvm.GNN;

/*
Just for testing of genetic algorithm
 */
public class ScoreEvaluator {

    public static void evaluate(double[] target, Client[] clients){
        for (int i = 0; i < clients.length; i++){
            double score = 50;

            int lenghtOfGn = 47;
            double[] genes = clients[i].getGenom().genom;
            for (int j = 0; j < lenghtOfGn; j++){
                score = score - Math.abs(target[j] - genes[j]);
            }

            clients[i].fitnessScore = score;
        }
    }
}
