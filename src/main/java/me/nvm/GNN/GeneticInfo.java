package me.nvm.GNN;

import java.util.Arrays;

public class GeneticInfo {
    int[] structure;
    public double[] genom;

    public GeneticInfo(int[] structure, double[] genom) {
        this.structure = structure;
        this.genom = genom;
    }

    @Override
    public String toString() {
        return "GeneticInfo{" +
                "structure=" + Arrays.toString(structure) +
                ", genom=" + Arrays.toString(genom) +
                '}';
    }
}
