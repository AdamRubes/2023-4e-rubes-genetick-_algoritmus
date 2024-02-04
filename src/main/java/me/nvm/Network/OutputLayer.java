package me.nvm.Network;

import java.util.Arrays;

public class OutputLayer implements Layer {
    public int size;
    public Neuron[] neurons;
    public double[] outputValues;
    Layer prevLayer;

    public OutputLayer(int size) {
        this.size = size;
        outputValues = new double[size];
    }

    @Override
    public void setNeurons(Neuron[] neurons) {
        this.neurons = neurons;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void compute() {
        for (int neuron = 0; neuron < size; neuron++){
            neurons[neuron].compute(prevLayer.getOutput());
            outputValues[neuron] = neurons[neuron].getValue();
        }
        //System.out.println(Arrays.toString(outputValues));
    }

    @Override
    public void linkLayer(Layer prevLayer, Layer nextLayer) {
        this.prevLayer = prevLayer;
    }

    @Override
    public double[] getOutput() {
        return outputValues;
    }

    @Override
    public Neuron[] getNeurons() {
        return neurons;
    }

    @Override
    public String toString() {
        return "\r\n" + "OutputLayer{" +
                "size=" + size +
                ", isLinked= " + (prevLayer!= null) +
                ", neurons=" + Arrays.toString(neurons) +
                '}';
    }
}
