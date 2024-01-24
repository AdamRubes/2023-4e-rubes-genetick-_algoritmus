package me.nvm.Network;

public class Neuron {
    public int numOfConn;
    public double[] inputWeights;
    public double bias;
    public double value;

    public Neuron(double bias, double[] inputWeights) {
        this.inputWeights = inputWeights;
        this.bias = bias;
        this.value = 0;
        this.numOfConn = inputWeights.length;
    }
    public void compute(double[] inputValues) {
        double sum = 0;
        sum += bias;
        for (int i = 0; i < inputValues.length; i++){
            sum += inputValues[i] * inputWeights[i];
        }
        value = SigmoidFun.compute(sum);
    }

    public double getValue(){
        return value;
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
