package me.nvm.Network;


import java.util.Arrays;

import static me.nvm.MainApp.AuxilaryTools.getRandomDouble;
import static me.nvm.MainApp.AuxilaryTools.randomize1DArray;


public class Network implements VisualisableFullyConnectedNetwork{
    public   Layer[] layers;
    public  int numOfLayers;
    public  int[] layerSizes;
    public  int inputSize;
    public  int outputSize;
    private int lastLayer;
    public double[] input;
    public double[] output;

    public double biasLowerBound = -2;
    public double biasUpperBound = +2;
    public double weightLowerBound = -2;
    public double weightUpperBound = +2;

    public Network(int... layerSizes) {
        this.layerSizes = layerSizes;
        this.numOfLayers = layerSizes.length;
        this.lastLayer = layerSizes.length - 1;

        this.inputSize = layerSizes[0];
        this.outputSize = layerSizes[lastLayer];
        this.layers = new Layer[numOfLayers];

        this.input = new double[inputSize];
        this.output = new double[outputSize];

        initLayers();
        initNeurons();
        linkLayers();
    }

    public void setInput(double[] input){
        this.input = input;
    }
    public void compute(){
        ((InputLayer)layers[0]).setValues(input);
        for(int layer = 0; layer < numOfLayers; layer++){
            layers[layer].compute();
        }
        output = layers[lastLayer].getOutput();
    }

    public double[] getOutput() {
        return output;
    }

    /*
            Fills the layers array with HiddenLayers. Except for the first and last layer.
            These two are being respectively initialised as Input and Output Layers.
        */
    private void initLayers(){
        layers[0] = new InputLayer(inputSize);
        for (int layer = 1; layer < layerSizes.length - 1; layer++){
            layers[layer] = new HiddenLayer(layerSizes[layer]);
        }
        layers[lastLayer] = new OutputLayer(outputSize);
    }



    /*
    Initiates neurons for all hidden layers and for output layer.
    */
    private void initNeurons(){
        Neuron[] inputNeurons = new Neuron[inputSize];

        Arrays.setAll(inputNeurons, i -> new Neuron(0, new double[layerSizes[1]]));

        layers[0].setNeurons(inputNeurons);

        //Hidden + output layers ->
        for (int layer = 1; layer < layerSizes.length; layer++){
            int sizeOfLayer = layerSizes[layer];
            int sizeOfPrevLayer = layerSizes[layer - 1];

            Neuron[] neurons = new Neuron[sizeOfLayer];

            for (int neuron = 0; neuron < sizeOfLayer; neuron++){
                double bias = getRandomDouble(biasLowerBound,biasUpperBound);
                double[] inputWeights = new double[sizeOfPrevLayer];
                randomize1DArray(inputWeights,weightLowerBound,weightUpperBound);

                neurons[neuron] = new Neuron(bias,inputWeights);
            }
            //TODO think if possible to replace it with interfacemethod
           layers[layer].setNeurons(neurons);
        }
    }

    private void linkLayers(){
        layers[0].linkLayer(null, layers[1]);
        for (int layer = 1; layer < layerSizes.length - 1; layer++){
            layers[layer].linkLayer(layers[layer - 1], layers[layer + 1]);
        }
        layers[lastLayer].linkLayer(layers[lastLayer - 1], null);
    }

    @Override
    public String toString() {
        return "Network{" +
                "numOfLayers=" + numOfLayers +
                ", layerSizes=" + Arrays.toString(layerSizes) +
                ", inputSize=" + inputSize +
                ", outputSize=" + outputSize +
                ", lastLayer=" + lastLayer +
                ", layers=" + Arrays.toString(layers) +
                '}';
    }


    public int getNumOfLayers() {
        return numOfLayers;
    }

    @Override
    public int[] getLayerSizes() {
        return layerSizes;
    }

    @Override
    public double[][] getOutputWeights(int layer) {
        return Arrays.stream(layers[layer].getNeurons())
                .map(Neuron::getInputWeights)
                .toArray(double[][]::new);
    }

    @Override
    public double[] getBiases(int layer) {

        return Arrays.stream(layers[layer].getNeurons())
                .findFirst()
                .map(neuron -> Arrays.stream(layers[layer].getNeurons())
                        .mapToDouble(Neuron::getBias)
                        .toArray())
                .orElseGet(() -> new double[layerSizes[0]]);
    }

    @Override
    public double[] getValues(int layer) {
        return Arrays.stream(layers[layer].getNeurons())
                .mapToDouble(Neuron::getValue)
                .toArray();
    }
}