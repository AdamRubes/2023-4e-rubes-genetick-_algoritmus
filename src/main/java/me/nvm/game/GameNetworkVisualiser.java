package me.nvm.game;

import me.nvm.GNN.GeneticInfo;
import me.nvm.GNN.GenomProcessor;
import me.nvm.MainApp.Resolution;
import me.nvm.Network.Network;
import me.nvm.Network.NetworkBuilder;
import me.nvm.Network.NetworkVisualiser;
import me.nvm.Network.VisualizableFullyConnectedNetwork;
import me.nvm.game.gameobjects.PipePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static me.nvm.MainApp.AuxilaryTools.getRandomNumber;
import static me.nvm.MainApp.AuxilaryTools.normalizeValue;

public class GameNetworkVisualiser extends JFrame {
    Network networkToVisualise;
    int pipeWidth = 50;
    ArrayList<PipePair> pipePairs =  new ArrayList<>();
    int frameWidth;
    int  frameHeight;
    double scale;
    int minUpper = 30;
    int maxUpper = 400;
    int sizeOfHole = 200;

    JButton closeButton;

    public GameNetworkVisualiser(Resolution resolution, Network network)  {
        setWindow(resolution);
        networkToVisualise = network;

        closeButton = new JButton("Stop game");
        closeButton.setBounds(0, 0, 100, 30);


        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        int a = 220;
        pipePairs.add(new PipePair(a, ((Resolution.SD.getHeight() - a) - sizeOfHole), Resolution.SD.getWidth()/3));
        pipePairs.add(new PipePair(a, ((Resolution.SD.getHeight() - a) - sizeOfHole), (Resolution.SD.getWidth()/3)+450));


        JSlider slider1 = new JSlider(JSlider.VERTICAL, 30, 400, 220);

        slider1.setBounds((int) (((Resolution.SD.getWidth()/3)-10) * scale), (int) ((minUpper + sizeOfHole/2) * scale), 20, (int) ((maxUpper + sizeOfHole/2)*scale - (minUpper + sizeOfHole/2) * scale));
        slider1.setOpaque(false);
        slider1.setInverted(true);
        slider1.addChangeListener(e -> {
            pipePairs.get(0).upPipe.coordinateY = slider1.getValue();
            pipePairs.get(0).downPipe.coordinateY = ((Resolution.SD.getHeight() - slider1.getValue()) - sizeOfHole);
            repaint();
        });

        JSlider slider2 = new JSlider(JSlider.VERTICAL, 30, 400, 220);

        slider2.setBounds((int) (((Resolution.SD.getWidth()/3)+450-10) * scale), (int) ((minUpper + sizeOfHole/2) * scale), 20, (int) ((maxUpper + sizeOfHole/2)*scale - (minUpper + sizeOfHole/2) * scale));
        slider2.setOpaque(false);
        slider2.setInverted(true);
        slider2.addChangeListener(e -> {
            pipePairs.get(1).upPipe.coordinateY = slider2.getValue();
            pipePairs.get(1).downPipe.coordinateY = ((Resolution.SD.getHeight() - slider2.getValue()) - sizeOfHole);
            repaint();
        });

        RenderingPanel renderingPanel = new RenderingPanel();
        renderingPanel.setLayout(null);
        renderingPanel.add(slider1);
        renderingPanel.add(slider2);
        renderingPanel.add(closeButton);
        add(renderingPanel);
    }

    private class RenderingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {

            for (int x = 0; x < Resolution.SD.getWidth(); x++) {
                for (int y = 0; y < Resolution.SD.getHeight(); y++) {
                    int deltaX = 0;
                    int upperPipe = 0;
                    int lowerPipe = 0;
                    if (x < pipePairs.get(0).position + pipeWidth / 2){
                        deltaX = (int) (pipePairs.get(0).position - x) + pipeWidth / 2;
                        upperPipe = pipePairs.get(0).getUpperY();
                        lowerPipe = pipePairs.get(0).getLowerY();
                        g.setColor(calculateColor(y, deltaX, upperPipe, lowerPipe));
                    }else  if (x < pipePairs.get(1).position + pipeWidth / 2){
                        deltaX = (int) (pipePairs.get(1).position - x + pipeWidth / 2);
                        upperPipe = pipePairs.get(1).getUpperY();
                        lowerPipe = pipePairs.get(1).getLowerY();
                        g.setColor(calculateColor(y, deltaX, upperPipe, lowerPipe));
                    }else  g.setColor(Color.DARK_GRAY);


                    g.fillRect(x, y, 1, 1);
                }
            }


            g.setColor(Color.GREEN);
            for (PipePair element : pipePairs) {
                int upperPipeHeight = (int) (element.getUpperY() * scale);
                int lowerPipeHeight = (int) (element.getLowerY() * scale);
                g.fillRect((int) ((element.position - pipeWidth/2) * scale), 0, (int) (pipeWidth * scale),  upperPipeHeight);
                g.fillRect((int) ((element.position - pipeWidth/2) * scale), frameHeight - lowerPipeHeight , (int) (pipeWidth * scale), lowerPipeHeight);
            }
        }
    }

    private Color calculateColor(int coordinateY, int deltaX, int upperPipe, int lowerPipe){
        double frstParam = normalizeValue(coordinateY, 0, getHeight());
        double secondParam = normalizeValue(deltaX, getWidth()/2, 0);
        double thirdParam = normalizeValue(upperPipe, minUpper, maxUpper);
        double fourthParam = normalizeValue(lowerPipe,((getHeight() - 400) - sizeOfHole),((getHeight() - 30) - sizeOfHole));

        double[] inputs = new double[]{frstParam, secondParam, thirdParam, fourthParam};
        networkToVisualise.setInput(inputs);
        networkToVisualise.compute();


        double output = networkToVisualise.getOutput()[0];

         return  convertToColor(output);
    }

    public static Color convertToColor(double value) {
        value = Math.max(0, Math.min(1, value));

        int red = (int) (255 * (1 - value));
        int green = 0;
        int blue = (int) (255 * value);

        return new Color(red, green, blue);
    }

    private void setWindow(Resolution resolution){
        this.scale = resolution.scaleFromSD;
        this.frameHeight = resolution.getHeight();
        this.frameWidth = resolution.getWidth();

        setTitle("Flapy Bird");
        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);


        try {
            setUndecorated(true);
        }catch (Exception e){}
    }

    private class NetworkVisualiserContainer extends JPanel{
        int keyOfCurrClient;
        NetworkVisualiser networkVisualiser;
        public NetworkVisualiserContainer(int x, int y, int keyOfCurrClient) {
            setLayout(null);
            setOpaque(false);



            this.keyOfCurrClient = keyOfCurrClient;

            //networkVisualiser = prepVisualiser(gameBackendAI.clientMap.get(keyOfCurrClient).network);

            setBounds(x, y, networkVisualiser.getWidth(),networkVisualiser.getHeight());

            add(networkVisualiser);
        }

        public void setNewClient(int keyOfCurrClient){
            remove(networkVisualiser);
            //networkVisualiser = prepVisualiser(gameBackendAI.clientMap.get(keyOfCurrClient).network);
            add(networkVisualiser);
        }

        private NetworkVisualiser prepVisualiser(VisualizableFullyConnectedNetwork network){
            NetworkVisualiser nv = new NetworkVisualiser(network);
            nv.setBounds(0,0,nv.calculateWidth(),nv.calculateHeight());
            return nv;
        }
    }

}
