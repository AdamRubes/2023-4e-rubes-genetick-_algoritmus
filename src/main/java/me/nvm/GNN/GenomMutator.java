package me.nvm.GNN;

import me.nvm.MainApp.AuxilaryTools;

import java.util.Arrays;

public class GenomMutator {
    // Range 0-1
    double mutationRate;
    double mutationStrength;
    @Deprecated
    public GenomMutator() {
        this.mutationRate = 0.08;
        this.mutationStrength = 0.07;
    }

    public GenomMutator(double mutationRate, double mutationStrength) {
        this.mutationRate = mutationRate;
        this.mutationStrength = mutationStrength;
    }

    public void mutate(GeneticInfo geneticInfo){
        double[] genom = geneticInfo.genom;

        for (int i = 0; i < genom.length; i++){
            double chanceOfMutation = AuxilaryTools.getRandomDouble(0,1);
            if(mutationRate > chanceOfMutation){
                genom[i] = genom[i] + (AuxilaryTools.getRandomGauss() * mutationStrength);
            }
        }
    }
}
