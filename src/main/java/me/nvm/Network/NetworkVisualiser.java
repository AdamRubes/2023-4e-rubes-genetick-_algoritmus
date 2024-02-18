package me.nvm.Network;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/*
    Nikdy se už nedotýkej následujícího kodu, ani si ho neprohlížej, jestli je ti život milý. Tvé minulé Já to stálo duševní zdraví.
    Některé hrůzy by měly zůstat navždy zapomenuty!
    Hodin strávených na tomto programátorském Verdunu: 16h - 03.02.2024

    PS: Chce se mi brečet když píšu tento komentář.
*/
public class NetworkVisualiser extends JPanel {
    VisualizableFullyConnectedNetwork network;

    int neuronDiameter = 40;
    int gapBetweenNeurons = 50; //Between centres of neurons ;P
    int gapBetweenLayers = 80;

    int[] structure;


    public NetworkVisualiser(VisualizableFullyConnectedNetwork network) {
        this.network = network;
        this.structure = network.getLayerSizes();
        setOpaque(false);
    }

    public int calculateWidth(){
        return 10 + structure.length * neuronDiameter + (structure.length-1) * gapBetweenLayers;
    }

    public int calculateHeight(){
        int sizeOfThickestLayer = Arrays.stream(structure)
                .max()
                .orElseThrow();

        return  10 + sizeOfThickestLayer * neuronDiameter + (sizeOfThickestLayer - 1) * gapBetweenNeurons;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int currLayer = 0; currLayer < structure.length; currLayer++){
            drawLayer(g, currLayer);

            if(currLayer > 0){
            drawConnections(g,currLayer);
            }
        }
    }

    private void drawLayer(Graphics g, int currLayer) {
        int x = currLayer * gapBetweenLayers;
        int y = getHeight() / 2;

        int layerSize = structure[currLayer];
        double[] biases = network.getBiases(currLayer);
        double[] values = network.getValues(currLayer);

        for (int currNeuron = 0; currNeuron < layerSize; currNeuron++) {
            double bias = biases[currNeuron];
            double value = values[currNeuron];

            Color color = getColorForValue(value);

            g.setColor(color);

            Graphics2D g2d = (Graphics2D) g;

            int borderWidth = 5;
            g2d.setStroke(new BasicStroke(borderWidth));

            g.fillOval(x, y - (layerSize / 2 * gapBetweenNeurons) + currNeuron * gapBetweenNeurons, neuronDiameter, neuronDiameter);

            g.setColor(getColorForWeight1(bias));
            g.drawOval(x, y - (layerSize / 2 * gapBetweenNeurons) + currNeuron * gapBetweenNeurons, neuronDiameter, neuronDiameter);

            g2d.setStroke(new BasicStroke(1));
        }
    }

    private Color getColorForValue(double value) {
        //FIXME něco udělat s normalizací, smazat maximum až se fixne backend
        double normalizedValue = Math.min(1.0, value);
        normalizedValue = Math.max(0.0, normalizedValue);

        if (value != normalizedValue) System.out.println("Value: " + value + " | Normalizováno" + normalizedValue);

        int alpha = (int) (255 * normalizedValue);
        int intensity = (int) (255 * normalizedValue);
        return new Color(intensity, intensity, intensity, alpha);
    }

    private void drawConnections(Graphics g, int currLayer) {
        int[] structure = network.getLayerSizes();
        int fromLayerSize = structure[currLayer-1];
        int toLayerSize = structure[currLayer];
        int fromX = (currLayer - 1) * gapBetweenLayers;
        int toX = currLayer * gapBetweenLayers;

        double[][] weights = network.getOutputWeights(currLayer);

//        System.out.println(Arrays.toString(structure));
//        System.out.println("Curr layer:" + currLayer);
//        System.out.println("FromLayerSize" + fromLayerSize);
//        System.out.println("toLayerSize" + toLayerSize);

        for (int fromNeuron = 0; fromNeuron < fromLayerSize; fromNeuron++) {
            for (int toNeuron = 0; toNeuron < toLayerSize; toNeuron++) {
                //System.out.println("FromNeuron: " + fromNeuron +", ToNeuron: " + toNeuron);
                double weight = weights[toNeuron][fromNeuron];

                Color color = getColorForWeight1(weight);
                g.setColor(color);

                g.drawLine(fromX + neuronDiameter, getHeight() / 2 - (weights[0].length / 2 * gapBetweenNeurons) + fromNeuron * gapBetweenNeurons + neuronDiameter / 2,
                        toX, getHeight() / 2 - (weights.length / 2 * gapBetweenNeurons) + toNeuron * gapBetweenNeurons + neuronDiameter / 2);
            }
        }
    }

    private Color getColorForWeight1(double weight) {
        double value = (weight + 3.0) / 6.0;

        Color red = new Color(255, 0, 0);
        Color white = new Color(255, 255, 255);
        Color green = new Color(0, 255, 0);

        int interpolatedRed, interpolatedGreen, interpolatedBlue;

        if (value <= 0.5) {
            double factor = value / 0.5;
            interpolatedRed = (int) ((1 - factor) * red.getRed() + factor * white.getRed());
            interpolatedGreen = (int) ((1 - factor) * red.getGreen() + factor * white.getGreen());
            interpolatedBlue = (int) ((1 - factor) * red.getBlue() + factor * white.getBlue());
        } else {
            double factor = (value - 0.5) / 0.5;
            interpolatedRed = (int) ((1 - factor) * white.getRed() + factor * green.getRed());
            interpolatedGreen = (int) ((1 - factor) * white.getGreen() + factor * green.getGreen());
            interpolatedBlue = (int) ((1 - factor) * white.getBlue() + factor * green.getBlue());
        }


        int alpha = (int) (255 * Math.abs(value - 0.5));
        if (alpha < 255 * 0.25) alpha = (int) (255 * 0.25);

        return new Color(interpolatedRed, interpolatedGreen, interpolatedBlue, alpha);
    }
}