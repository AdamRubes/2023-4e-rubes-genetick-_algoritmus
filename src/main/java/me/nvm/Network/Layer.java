package me.nvm.Network;

public interface Layer {
    public int getSize();
    public void compute();
    public void linkLayer(Layer prevLayer, Layer nextLayer);
    public double[] getOutput();

    public void setNeurons(Neuron[] neurons);

    public Neuron[] getNeurons();
}
