package me.nvm.game;

import me.nvm.GNN.Client;
import me.nvm.MainApp.Resolution;
import me.nvm.Network.Network;
import me.nvm.Network.NetworkVisualiser;
import me.nvm.Network.VisualizableFullyConnectedNetwork;
import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

import javax.swing.*;
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

        NetworkVisualiserContainer networkVisualiserContainer = new NetworkVisualiserContainer(0,30,1);
        NetworkVisualiserContainer networkVisualiserContainer2 = new NetworkVisualiserContainer(0,30,2);
        NetworkVisualiserContainer networkVisualiserContainer3 = new NetworkVisualiserContainer(0,30,3);

        JPanel visualiserBox = new JPanel();
        visualiserBox.setOpaque(false);
        visualiserBox.setBounds(0,40, networkVisualiserContainer.getWidth(), getHeight() - 40);
        visualiserBox.setLayout(new BoxLayout(visualiserBox, BoxLayout.Y_AXIS));

        visualiserBox.add(networkVisualiserContainer);
        visualiserBox.add(networkVisualiserContainer2);
        visualiserBox.add(networkVisualiserContainer3);
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

                    g.setColor(Color.RED);
                    g.fillRect((int) ((bird.coordinateX - (birdWidth / 2)) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) (birdWidth * scale), (int) (birdHeight * scale));

                    g.setColor(Color.BLUE);
                    int circleRadius = 25;
                    g.fillOval((int) ((bird.coordinateX - circleRadius) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) ((circleRadius * 2) * scale), (int) ((circleRadius * 2) * scale));

                    g.setColor(Color.BLACK);
                    String keyText = String.valueOf(birdEntry.getKey());
                    int textX = (int) ((bird.coordinateX - (birdWidth / 2)) * scale) + 5;
                    int textY = (int) ((bird.coordinateY - (birdHeight / 2)) * scale) + 15;
                    g.drawString(keyText, textX, textY);
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
    }

    private class NetworkVisualiserContainer extends JPanel{
        JComboBox<String> comboBox = new JComboBox<>();
        NetworkVisualiser networkVisualiser;
        public NetworkVisualiserContainer(int x, int y, int defaultItem) {

            refreshClients();
            addListeners();

            setLayout(null);
            setOpaque(false);

            networkVisualiser = prepVisualiser(gameBackendAI.clientMap.get(getKeyAtPosition(defaultItem)).network);

            comboBox.setBounds(0,0,200,30);
            setBounds(x, y, networkVisualiser.getWidth(),networkVisualiser.getHeight()+40);

            add(comboBox);
            add(networkVisualiser);
        }

        public void refreshClients(){
            ActionListener[] listeners = comboBox.getActionListeners();
            for (ActionListener listener : listeners) {
                comboBox.removeActionListener(listener);
            }


            comboBox.removeAllItems();


            birdMap.keySet()
                    .stream()
                    .map(integer -> String.valueOf(integer))
                    .forEach(comboBox::addItem);



            for (ActionListener listener : listeners) {
                comboBox.addActionListener(listener);
            }
        }

        public static void printComboBoxItems(JComboBox<?> comboBox) {
            ComboBoxModel<?> model = comboBox.getModel();
            int size = model.getSize();
            for (int i = 0; i < size; i++) {
                Object item = model.getElementAt(i);
                System.out.println(item);
            }
        }

        private void addListeners(){

            comboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    int selectedItem = Integer.parseInt((String) comboBox.getSelectedItem());

                    System.out.println("In action listener" + selectedItem);
                    remove(networkVisualiser);
                    networkVisualiser = prepVisualiser(gameBackendAI.clientMap.get(selectedItem).network);
                    add(networkVisualiser);
                }
            });
            comboBox.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    refreshClients();
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {}

            });
        }

        private NetworkVisualiser prepVisualiser(VisualizableFullyConnectedNetwork network){
            NetworkVisualiser nv = new NetworkVisualiser(network);
            nv.setBounds(0,30,nv.calculateWidth(),nv.calculateHeight());
            return nv;
        }


        public int getKeyAtPosition(int position) {
            Set<Integer> keySet = gameBackendAI.clientMap.keySet();
                Integer[] keysArray = keySet.toArray(new Integer[]{});
                return keysArray[position];

        }
    }
}
