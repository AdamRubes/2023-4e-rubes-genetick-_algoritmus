package me.nvm.Network;

public class Neuron {
    ActivationFunction activationFunction;
    public int numOfConn;
    public double[] inputWeights;
    public double bias;
    public double value;

    public Neuron(double bias, double[] inputWeights, ActivationFunction function) {
        this.inputWeights = inputWeights;
        this.bias = bias;
        this.value = 0;
        this.numOfConn = inputWeights.length;
        this.activationFunction = function;
    }
    public void compute(double[] inputValues) {
        double sum = 0;
        sum += bias;
        for (int i = 0; i < inputValues.length; i++){
            sum += inputValues[i] * inputWeights[i];
        }
        value = activationFunction.compute(sum);
    }

    public double getValue(){
        return value;
    }

    public double getBias(){
        return bias;
    }

    public double[] getInputWeights() {
        return inputWeights;
    }

    @Override
    public String toString() {
        return "Neuron{" +
                "inputWeights lenght=" + inputWeights.length +
                //"inputWeights=" + Arrays.toString(inputWeights) +
                ", bias=" + bias +
                '}';
    }
}
