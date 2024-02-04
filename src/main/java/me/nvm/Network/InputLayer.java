package me.nvm.Network;

/*
Class representing the first (input) layer of the neural network.
The purpose of this layer is just to pass input values to the first hidden layer.

 */

public class InputLayer implements Layer {
    public int size;

    Layer nextLayer;

    public double[] values;

    Neuron[] lobotomizedNeurons;

    public InputLayer(int size) {
        this.size = size;
        this.values = new double[size];
    }

    public void setValues(double[] values){
        this.values = values;
        for (int i = 0; i < lobotomizedNeurons.length; i++){
            lobotomizedNeurons[i].value = values[i];
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    /*
    No computations are being made here.

     */
    @Override
    public void compute() {
        //System.out.println(Arrays.toString(values));
    }

    @Override
    public void linkLayer(Layer prevLayer, Layer nextLayer) {
        this.nextLayer = nextLayer;
    }

    @Override
    public double[] getOutput() {
        return values;
    }

    @Override
    public void setNeurons(Neuron[] neurons) {
        this.lobotomizedNeurons = neurons;
    }

    @Override
    public Neuron[] getNeurons() {
        return lobotomizedNeurons;
    }

    @Override
    public String toString() {
        return "\r\n" + "InputLayer{" +
                "size=" + getSize() +
                ", isLinked= " + (nextLayer != null) +
                '}'
                ;
    }
}
