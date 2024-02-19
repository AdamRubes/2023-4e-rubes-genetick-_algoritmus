package me.nvm.Network;

public class LayerBluePrint {
        final public ActivationFunction function;
        final public int layerSize;

        public LayerBluePrint(int layerSize, ActivationFunction function) {
            this.layerSize = layerSize;
            this.function = function;
        }

        public ActivationFunction getFunction() {
            return function;
        }

        public int getLayerSize() {
            return layerSize;
        }
}


