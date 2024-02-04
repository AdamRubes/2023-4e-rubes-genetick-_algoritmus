package me.nvm.Network;

public class ReLu implements ActivationFunction{
    @Override
    public double compute(double input) {
        if(input > 0) return input;
        else return 0;
    }
}
