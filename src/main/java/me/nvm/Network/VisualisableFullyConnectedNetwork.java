package me.nvm.Network;

public interface VisualisableFullyConnectedNetwork  {

    public int[] getLayerSizes();

    public double[][] getOutputWeights(int layer);

    public double[] getBiases(int layer);

    public double[] getValues(int layer);
}
