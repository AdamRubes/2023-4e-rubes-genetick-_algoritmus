package me.nvm.Network;

public class SigmoidFun {

public static double compute(double value){
    return 1 / (1 + Math.exp(-value));
}
}
