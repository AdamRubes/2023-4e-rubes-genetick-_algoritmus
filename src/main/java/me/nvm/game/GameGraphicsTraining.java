package me.nvm.game;

import me.nvm.GNN.Client;
import me.nvm.MainApp.Resolution;
import me.nvm.Network.Network;
import me.nvm.Network.NetworkVisualiser;
import me.nvm.Network.VisualizableFullyConnectedNetwork;
import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class GameGraphicsTraining extends JFrame implements GraphicsInterface {
    Map<Integer, Bird> birdMap;
    RenderingPanel makeShitCanvas;
    GameBackendAI gameBackendAI;
    Queue<PipePair> pipePairs;
    double scale;
    int frameWidth;
    int  frameHeight;
    JButton closeButton;
    GameState gameState;

    List<NetworkVisualiser> networkVisualisers = new ArrayList<>();

    HashMap<Integer, NetworkVisualiserContainer> visualiserContainerHashMap = new HashMap<>();
    HashMap<Integer, Bird> birdsToBeColored = new HashMap<>();

    JPanel visualiserBox;

    public GameGraphicsTraining(GameBackendAI gameBackendAI, Resolution resolution){
        setWindow(resolution);

        gameState = GameState.getInstance();

        closeButton = new JButton("Stop game");
        closeButton.setBounds(0, 0, 100, 30);


        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameState.setGameOver(true);
            }
        });

        this.gameBackendAI = gameBackendAI;
        birdMap = gameBackendAI.birdMap;
        pipePairs = gameBackendAI.pipePairs;

        makeShitCanvas = new RenderingPanel();



        makeShitCanvas.setLayout(null);
        makeShitCanvas.add(closeButton);

        visualiserBox = new JPanel();
        visualiserBox.setOpaque(false);
        visualiserBox.setLayout(new BoxLayout(visualiserBox, BoxLayout.Y_AXIS));
        visualiserBox.setBounds(0,30,600,getHeight());





        makeShitCanvas.add(visualiserBox);
        add(makeShitCanvas);
    }

    public void registerVisualiser(NetworkVisualiser visualiser){

        //visualiser.setBounds(10, 15, visualiser.calculateWidth(), visualiser.calculateHeight());
        //makeShitCanvas.add(visualiser);
    }

    @Override
    public void showWindow() {
        setVisible(true);
    }
    @Override
    public void refresh(){
        makeShitCanvas.repaint();
    }

    @Override
    public void closeWindow() {
        dispose();
    }

    @Override
    public void setResolution(Resolution resolution) {
        setWindow(resolution);

    }

    public void initialiseVisualiser(int keyOfVisualiser, int keyOfNewClient){
        System.out.println("inited with:" + keyOfVisualiser);
        visualiserContainerHashMap.put(keyOfVisualiser, new NetworkVisualiserContainer(0,0,keyOfNewClient));

        setBorderColor(keyOfVisualiser);

        birdsToBeColored.put(keyOfVisualiser,birdMap.get(keyOfNewClient));
        visualiserBox.add(visualiserContainerHashMap.get(keyOfVisualiser));
        visualiserBox.revalidate();
    }

    public void setVisualiser(int keyOfVisualiser, int keyOfNewClient){
        birdsToBeColored.put(keyOfVisualiser,birdMap.get(keyOfNewClient));

        visualiserContainerHashMap.get(keyOfVisualiser).setNewClient(keyOfNewClient);
        setBorderColor(keyOfVisualiser);
    }

    public void setBorderColor(int keyOfVisualiser){
        Border border = null;
        if (keyOfVisualiser == 1){
            border = BorderFactory.createLineBorder(Color.MAGENTA);
        } else if (keyOfVisualiser == 2) {
            border = BorderFactory.createLineBorder(Color.YELLOW);
        } else if (keyOfVisualiser == 3) {
            border = BorderFactory.createLineBorder(Color.GREEN);
        }
        visualiserContainerHashMap.get(keyOfVisualiser).networkVisualiser.setBorder(border);
    }

    public void hideVisualiser(int visualiserToBeHidden){
        //FIXME fixnout aby se to furt nevolalo. Není čas opravovat, nezá se být podstatné:D
        visualiserBox.remove(visualiserContainerHashMap.get(visualiserToBeHidden));
        birdsToBeColored.remove(visualiserToBeHidden);
        visualiserBox.revalidate();
    }

    private void setWindow(Resolution resolution){
        this.scale = resolution.scaleFromSD;
        this.frameHeight = resolution.getHeight();
        this.frameWidth = resolution.getWidth();

        setTitle("Flapy Bird");
        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        if (visualiserBox != null){
            visualiserBox.setBounds(0,30,600,getHeight());
            visualiserBox.revalidate();
        }


        try {
            setUndecorated(true);
        }catch (Exception e){}
    }


    private class RenderingPanel extends JPanel {
        int birdWidth = 50;
        int birdHeight = 50;
        int pipeWidth = 50;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);

            synchronized (birdMap) {




                for (Map.Entry<Integer, Bird> birdEntry : birdMap.entrySet()) {
                    Bird bird = birdEntry.getValue();
                    if (!birdsToBeColored.containsValue(bird)) {
                        g.setColor(Color.RED);
                        paintBird(g, bird);
                    }
                }

                for (Map.Entry<Integer, Bird> birdEntry : birdsToBeColored.entrySet()) {
                    Bird bird = birdEntry.getValue();

                    g.setColor(getBirdColor(bird));

                    paintBird(g, bird);
                }
            }

            synchronized (pipePairs) {
                g.setColor(Color.GREEN);
                for (PipePair element : pipePairs) {
                    int upperPipeHeight = (int) (element.getUpperY() * scale);
                    int lowerPipeHeight = (int) (element.getLowerY() * scale);
                    g.fillRect((int) ((element.position - pipeWidth / 2) * scale), 0, (int) (pipeWidth * scale), upperPipeHeight);
                    g.fillRect((int) ((element.position - pipeWidth / 2) * scale), frameHeight - lowerPipeHeight, (int) (pipeWidth * scale), lowerPipeHeight);
                }
            }
        }

        private void paintBird(Graphics g, Bird bird) {
            g.fillRect((int) ((bird.coordinateX - (birdWidth / 2)) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) (birdWidth * scale), (int) (birdHeight * scale));

            g.setColor(Color.BLUE);
            int circleRadius = 25;
            g.fillOval((int) ((bird.coordinateX - circleRadius) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) ((circleRadius * 2) * scale), (int) ((circleRadius * 2) * scale));

            g.setColor(Color.BLACK);

            /*
            String keyText = String.valueOf(birdEntry.getKey());
            int textX = (int) ((bird.coordinateX - (birdWidth / 2)) * scale) + 5;
            int textY = (int) ((bird.coordinateY - (birdHeight / 2)) * scale) + 15;
            g.drawString(keyText, textX, textY);

             */
        }

        private Color getBirdColor(Bird bird) {
            if (birdsToBeColored.get(1) == bird) return Color.MAGENTA;
            else if (birdsToBeColored.get(2) == bird) return Color.YELLOW;
            else if (birdsToBeColored.get(3) == bird) return Color.GREEN;
            else return Color.RED;
        }



    }

    private class NetworkVisualiserContainer extends JPanel{
        int keyOfCurrClient;
        NetworkVisualiser networkVisualiser;
        public NetworkVisualiserContainer(int x, int y, int keyOfCurrClient) {
            setLayout(null);
            setOpaque(false);



            this.keyOfCurrClient = keyOfCurrClient;
            networkVisualiser = prepVisualiser(gameBackendAI.clientMap.get(keyOfCurrClient).network);

            setBounds(x, y, networkVisualiser.getWidth(),networkVisualiser.getHeight());

            add(networkVisualiser);
        }

        public void setNewClient(int keyOfCurrClient){
            remove(networkVisualiser);
            networkVisualiser = prepVisualiser(gameBackendAI.clientMap.get(keyOfCurrClient).network);
            add(networkVisualiser);
        }

        private NetworkVisualiser prepVisualiser(VisualizableFullyConnectedNetwork network){
            NetworkVisualiser nv = new NetworkVisualiser(network);
            nv.setBounds(0,0,nv.calculateWidth(),nv.calculateHeight());
            return nv;
        }
    }
}
