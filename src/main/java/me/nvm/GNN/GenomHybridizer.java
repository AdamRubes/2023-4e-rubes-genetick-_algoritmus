package me.nvm.GNN;


import me.nvm.MainApp.AuxilaryTools;

import java.util.Arrays;

public class GenomHybridizer {
    public static GeneticInfo uniformCrossover(GeneticInfo geneticInfo1, GeneticInfo geneticInfo2){

        double[] genom1 = geneticInfo1.genom;
        double[] genom2 = geneticInfo2.genom;


        if (!Arrays.equals(geneticInfo1.structure,geneticInfo2.structure)){
            System.out.println("Rýýýýýýýýýýýýýýýýý");
            System.out.println(Arrays.toString(geneticInfo1.structure));
            System.out.println(Arrays.toString(geneticInfo2.structure));
            return null;
        }

        int length = genom1.length;
        double[] hybrid = new double[length];

        for (int i = 0; i < length; i++){
            double gene;
            double randomizer = AuxilaryTools.getRandomNumber(-1,1);

            if(randomizer < 0) gene = genom1[i];
            else gene = genom2[i];

            hybrid[i] = gene;
        }
        return new GeneticInfo(geneticInfo1.structure, hybrid);
    }
}

//ToDo Add another crossover methods
// One-point crossover, Two-point and k-point crossover
