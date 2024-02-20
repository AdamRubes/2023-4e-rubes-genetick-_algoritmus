package me.nvm.Network;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
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
        return neuronDiameter + (structure.length-1) * gapBetweenLayers;
    }

    public int calculateHeight(){
        int sizeOfThickestLayer = Arrays.stream(structure)
                .max()
                .orElseThrow();

        if ((sizeOfThickestLayer % 2) == 0){
            return (int) (neuronDiameter * 1.5 + (sizeOfThickestLayer - 1) * gapBetweenNeurons);
        }else {
            return (int) ( neuronDiameter  + (sizeOfThickestLayer - 1) * gapBetweenNeurons);
        }
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
        ActivationFunction activationFunction = network.getActivationFun(currLayer);

        for (int currNeuron = 0; currNeuron < layerSize; currNeuron++) {
            double bias = biases[currNeuron];
            double value = values[currNeuron];
            Color color;
            if (activationFunction instanceof SigmoidFun) color = getColorForValueSigmoid(value);
            else if (activationFunction instanceof ReLu) color = getColorForValueReLU(value);
            else{
                //System.out.println("Divné");
                color = getColorForValueSigmoid(value);
            }

            color = getColorForValueReLU(value);

            g.setColor(color);

            Graphics2D g2d = (Graphics2D) g;

            int borderWidth = 5;
            g2d.setStroke(new BasicStroke(borderWidth));

            if ((layerSize%2) ==0){
                g.fillOval(x, y - (layerSize / 2 * gapBetweenNeurons) + currNeuron * gapBetweenNeurons, neuronDiameter, neuronDiameter);

                g.setColor(getColorForWeight1(bias));
                g.drawOval(x, y - (layerSize / 2 * gapBetweenNeurons) + currNeuron * gapBetweenNeurons, neuronDiameter, neuronDiameter);

            }else {
                g.fillOval(x, (int) (y - (((layerSize / 2) + 0.5) * gapBetweenNeurons) + currNeuron * gapBetweenNeurons), neuronDiameter, neuronDiameter);

                g.setColor(getColorForWeight1(bias));
                g.drawOval(x, (int) (y -(((layerSize / 2) + 0.5)  *  gapBetweenNeurons) + currNeuron * gapBetweenNeurons), neuronDiameter, neuronDiameter);
            }


            g2d.setStroke(new BasicStroke(1));
        }
    }

    private Color getColorForValueReLU(double value) {
        //FIXME něco udělat s normalizací, smazat maximum až se fixne backend
        double normalizedValue =Math.min(1.0, Math.max(0.0, value));
        if (normalizedValue != value) {
            //System.out.println("Error Relu- " + value + "->" + normalizedValue);
        }

        int intensity = (int) (255 * normalizedValue);
        /*
        System.out.println("Error ReLU");
        System.out.println("value" + value);
        System.out.println("intensity" + intensity);

         */
        return new Color(intensity, intensity, intensity);
    }

    private Color getColorForValueSigmoid(double value) {
        //FIXME něco udělat s normalizací, smazat maximum až se fixne backend
        double normalizedValue = Math.min(1.0, Math.max(0.0, value));
        if (normalizedValue != value){
           // System.out.println("Error Sigmoid - " +value+  "->" +normalizedValue );
        }

        int intensity = (int) (255 * normalizedValue);
        /*
            System.out.println("Sigmoid");
            System.out.println("Value=" + value);
            System.out.println("Intensity=" + intensity);

         */
        return new Color(intensity, intensity, intensity);
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
                int x1 = fromX + neuronDiameter;
                int x2 = toX;
                int y1;
                int y2;

                if ((fromLayerSize % 2) == 0) {
                    y1 = getHeight() / 2 - (weights[0].length / 2 * gapBetweenNeurons) + fromNeuron * gapBetweenNeurons + neuronDiameter / 2;
                }else {
                    y1 = (int) (getHeight() / 2 - (((weights[0].length / 2) + 0.5) * gapBetweenNeurons) + fromNeuron * gapBetweenNeurons + neuronDiameter / 2);
                }


                if ((toLayerSize%2) == 0){
                    y2 = getHeight() / 2 - (weights.length / 2 * gapBetweenNeurons) + toNeuron * gapBetweenNeurons + neuronDiameter / 2;
                }else {
                    y2 = (int) (getHeight() / 2 - (((weights.length / 2) + 0.5) * gapBetweenNeurons) + toNeuron * gapBetweenNeurons + neuronDiameter / 2);
                }


                g.drawLine(x1, y1, x2, y2);

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