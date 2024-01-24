package me.nvm.Network;

/*
Class representing the first (input) layer of the neural network.
The purpose of this layer is just to pass input values to the first hidden layer.
In this implementation you have to first set input values and then the compute function
will start a recursive computing in the next and next.. layer.
 */

public class InputLayer implements Layer {
    public int size;

    Layer nextLayer;

    public double[] values;

    public InputLayer(int size) {
        this.size = size;
        this.values = new double[size];
    }

    public void setValues(double[] values){
        this.values = values;
    }

    @Override
    public int getSize() {
        return size;
    }

    /*
    No computations are being made here.
    Just calls compute method for the first hidden layer.
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
    public Neuron[] getNeurons() {
        return null;
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
