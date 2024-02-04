package me.nvm.Network;

public class SigmoidFun implements ActivationFunction{
@Override
public double compute(double value){
    return 1 / (1 + Math.exp(-value));
}
}
