package me.nvm.MainApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import me.nvm.GNN.Client;
import me.nvm.GNN.GeneticInfo;
import me.nvm.GNN.GenomProcessor;
import me.nvm.Network.Network;
import me.nvm.game.Game;
import me.nvm.game.GameNetworkVisualiser;
import me.nvm.game.GameState;
import org.controlsfx.control.ListSelectionView;
import org.controlsfx.control.action.Action;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import static me.nvm.GNN.GenomProcessor.loadGenom;
import static me.nvm.MainApp.AuxilaryTools.getFilenamesInDirectory;

public class MainWindowController {

    Game game;

    Training training;

    File folderWithGenomes;

    @FXML
    private AnchorPane toBeStyled;

    @FXML
    private AnchorPane GameControlsPane;

    @FXML
    private AnchorPane MainPane;

    @FXML
    private TextField elitesToBeSavedTextField;

    @FXML
    private CheckBox saveGenomsBox;

    @FXML
    private TextField numElitesTextField;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label birdsLabel;

    @FXML
    private Label engineFPSLabel;

    @FXML
    private Label uiFPSLabel;

    @FXML
    private TextField numRunsTextField;

    @FXML
    private TextField sizeGenerationTextField;

    @FXML
    private TextField structureTextField;

    @FXML
    private TextField rateMutationText;

    @FXML
    private TextField strengthMutationText;

    @FXML
    private ComboBox<Resolution> gameResolutionBox;

    @FXML
    private ComboBox<Double> gameSpeedBox;

    @FXML
    private ListSelectionView<Label> clientList;

    @FXML
    private ListSelectionView<Label> clientListVisualise;

    @FXML
    private CheckBox headlessCheck;

    @FXML
    private Label pathLabel;

    @FXML
    private Label pathLabel1;

    @FXML
    private ProgressBar currRunProgressBar;

    @FXML
    private ProgressBar runsProgrssBar;

    @FXML
    void startGameOnAction(ActionEvent event) {
        if(game.isRunning()){
            System.out.println("Hra bezi");
            return;
        }
        prepareGame();
        game.startPlayerGameReworked();
    }

    @FXML
    void endGameOnAction(ActionEvent event) {
        game.stopGame();
    }

    @FXML
    void pauseGameOnAction(ActionEvent event) {
        //ToDo Implement
        System.out.println("TO be implemented");
    }

    @FXML
    void StartLoadedGameOnAction(ActionEvent event) {
        if(game.isRunning()){
            System.out.println("Hra bezi");
            return;
        }
        prepareGame();

        if(clientList.getTargetItems().isEmpty()){
            return;
        }

        HashMap<Integer,Client> clientHashMap = new HashMap<>();

        for (Label label: clientList.getTargetItems()){
            String name = label.getText();

            GeneticInfo geneticInfo = loadGenom(FolderHolder.customFolder.getAbsolutePath()+"/" + name);
            Client client = new Client(geneticInfo);

            clientHashMap.put(client.id,client);
        }

        game.startTrainingGame(clientHashMap, 0);
    }

    @FXML
    void refreshListOnAction(ActionEvent event) {
        clientList.getSourceItems().clear();
        clientList.getTargetItems().clear();
        refreshList();
    }

    @FXML
    void refreshListVisualiseOnAction(ActionEvent event) {
        clientListVisualise.getSourceItems().clear();
        clientListVisualise.getTargetItems().clear();
        refreshListVisualise();
    }

    @FXML
    void startTrainingOnAction(ActionEvent event) {
        if(game.isRunning()){
            System.out.println("Hra bezi");
            return;
        }

        int sizeOfGeneration = Integer.parseInt(sizeGenerationTextField.getText());
        int numberOfElites = Integer.parseInt(numElitesTextField.getText());
        int numberOfRuns = Integer.parseInt(numRunsTextField.getText());
        double mutationStrength = Double.parseDouble(strengthMutationText.getText());
        double mutationRate = Double.parseDouble(rateMutationText.getText());
        int numOfElitesPrinted = prepSaves();

        String structureStr = structureTextField.getText();

        String[] stringArray = structureStr.split(",");

        int[] structure = new int[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            structure[i] = Integer.parseInt(stringArray[i].trim());
        }

        Resolution resolution = gameResolutionBox.getValue();

/*
        System.out.println(sizeOfGeneration);
        System.out.println(numberOfElites);
        System.out.println(numberOfRuns);
        System.out.println(mutationStrength);
        System.out.println(mutationRate);
        System.out.println(Arrays.toString(structure));
        System.out.println(resolution.getWidth());
        System.out.println(resolution.getHeight());
 */

        System.out.println(numOfElitesPrinted);
        prepareGame();

        //ToDo ukládání genomů regulace

        training = new TrainingBuilder()
                .setSizeOfGeneration(sizeOfGeneration)
                .setNumOfElites(numberOfElites)
                .setNumOfRuns(numberOfRuns)
                .setStructure(structure)
                .setMutationRate(mutationRate)
                .setMutationStrength(mutationStrength)
                .setNumOfElitesPrinted(numOfElitesPrinted)
                .linkAddGameObject(game)
                .build();

        runsProgrssBar.progressProperty().bind(training.progress);


        training.start();
    }

    public void prepareGame(){

        game = new GameBuilder()
                .setPredefinedRules()
                .setHeadless(headlessCheck.isSelected())
                .setSpeedMultiplier(gameSpeedBox.getValue())
                .setResolution(gameResolutionBox.getValue())
                .build();

        bindFPSCounters();
    }

    @FXML
    void saveGenomsOnAction(ActionEvent event) {
        if (training != null){
            training.numOfElitesPrinted = prepSaves();
        }
    }

    @FXML
    void elitesToBeSavedOnAction(ActionEvent event) {
        training.numOfElitesPrinted = prepSaves();
    }

    @FXML
    void stopTrainingOnAction(ActionEvent event) {
        training.stopAfterCurrLoop();
    }

    @FXML
    void stopNowTrainingOnAction(ActionEvent event) {
        if (training != null){
            training.stopTrainingNow();
        }
    }

    @FXML
    void endRunOnAction(ActionEvent event) {
        training.stopCurrRun();
    }

    @FXML
    void gameSpeedChanged(ActionEvent event) {if (game != null) game.changeSpeedOfGame(gameSpeedBox.getValue());
    }

    @FXML
    void resolutionChanged(ActionEvent event) {if (game != null)  game.changeResolution(gameResolutionBox.getValue());}

    @FXML
    public void changeDirectoryOnAction(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(FolderHolder.USER_HOME);

        FolderHolder.customFolder = directoryChooser.showDialog(new Stage());
        pathLabel.setText(FolderHolder.customFolder.getPath());

        refreshList();
    }

    @FXML
    public void changeDirectoryVisualiseOnAction(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(FolderHolder.USER_HOME);

        FolderHolder.customFolder = directoryChooser.showDialog(new Stage());
        pathLabel1.setText(FolderHolder.customFolder.getPath());

        refreshListVisualise();
    }

    @FXML
    public void startVisualisation(){
        if(clientListVisualise.getTargetItems().isEmpty()){
            return;
        }

        Label label = clientListVisualise.getTargetItems().get(0);
        String name = label.getText();
        GeneticInfo geneticInfo = loadGenom(FolderHolder.customFolder.getAbsolutePath()+"/" + name);
        Network network = GenomProcessor.decodeGenom(geneticInfo);

        GameNetworkVisualiser gameNetworkVisualiser = new GameNetworkVisualiser(Resolution.SD, network);

        gameNetworkVisualiser.setVisible(true);
        gameNetworkVisualiser.repaint();
    }

    public int prepSaves(){
        int numOfElitesPrinted;
        if(saveGenomsBox.isSelected()){
            if (elitesToBeSavedTextField.getText() != ""){
                numOfElitesPrinted = Integer.parseInt(elitesToBeSavedTextField.getText());
            }else{
                System.out.println("invalid");
                numOfElitesPrinted = 0;
            }
        }else numOfElitesPrinted = 0;

        return numOfElitesPrinted;
    }

    public void refreshList(){
        List<String> clientSource = getFilenamesInDirectory(FolderHolder.customFolder.getPath());
        clientSource.remove("forGitUwU");

        ObservableList<Label> observableClientSourceLabels = FXCollections.observableArrayList();

        for (String fileName : clientSource) {
            Label label = new Label(fileName);
            observableClientSourceLabels.add(label);
        }
        clientList.getSourceItems().addAll(observableClientSourceLabels);
    }

    public void refreshListVisualise(){
        List<String> clientSource = getFilenamesInDirectory(FolderHolder.customFolder.getPath());
        clientSource.remove("forGitUwU");

        ObservableList<Label> observableClientSourceLabels = FXCollections.observableArrayList();

        for (String fileName : clientSource) {
            Label label = new Label(fileName);
            observableClientSourceLabels.add(label);
        }
        clientListVisualise.getSourceItems().addAll(observableClientSourceLabels);
    }

    public void bindFPSCounters(){
        engineFPSLabel.textProperty().bind(game.engineFPS);
        uiFPSLabel.textProperty().bind(game.uiFPS);
    }

    public void initialize() {
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setParent(toBeStyled);

        FolderHolder.autoInitFile();

        pathLabel.setText(FolderHolder.customFolder.getPath());
        pathLabel1.setText(FolderHolder.customFolder.getPath());

        GameState gameState = GameState.getInstance();

        scoreLabel.textProperty().bind(gameState.scoreProperty.asString());

        birdsLabel.textProperty().bind(gameState.numOfLivingBirdsProperty.asString());

        currRunProgressBar.visibleProperty().bind(gameState.isGameRunning);



        refreshList();
        refreshListVisualise();

        gameResolutionBox.getItems().addAll(
                Resolution.nHD, Resolution.SD, Resolution.FHD, Resolution.WQHD, Resolution.UHD_4K
        );
        gameResolutionBox.setValue(Resolution.SD);

        gameSpeedBox.getItems().addAll(
                0.1, 0.5, 1.0, 1.5, 2.0, 4.0
        );

        gameSpeedBox.setValue(1.0);

        prepareGame();
    }
}