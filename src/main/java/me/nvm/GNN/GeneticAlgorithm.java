package me.nvm.GNN;



import me.nvm.MainApp.AuxilaryTools;
import me.nvm.MainApp.FolderHolder;

import java.util.Arrays;

public class GeneticAlgorithm {
    int numOfElite;
    Client[] elite;
    GenomMutator genomMutator;

    public GeneticAlgorithm() {
        this.numOfElite = 10;
        this.elite = new Client[numOfElite];
        this.genomMutator = new GenomMutator();
    }


    public GeneticAlgorithm(int numOfElite, double mutationRate, double mutationStrength) {
        this.numOfElite = numOfElite;
        this.elite = new Client[numOfElite];
        this.genomMutator = new GenomMutator(mutationRate,mutationStrength);
    }

    public Client[] evolve(Client[] inputClients){

        elite = chooseElite(inputClients);

/*
        System.out.println("Elite: ");
        for (Client client: elite){
            System.out.println(client.toString());
        }

        saveGenomOfElites(numOfElite);
        System.out.println("===========");
 */

        GeneticInfo[] superiorGenes = getGeneticInfo(elite);

        GeneticInfo[] newGenePool = createNewGenePool(superiorGenes, inputClients.length);

        mutateGenePool(newGenePool);

        Client[] newGeneration = assembleNewGeneration(newGenePool);

        return newGeneration;
    }


    private Client[] chooseElite(Client[] inputClients){
        Arrays.sort(inputClients);

        return Arrays.copyOfRange(inputClients,0, numOfElite);
    }

    private GeneticInfo[] getGeneticInfo(Client[] inputClients){
        GeneticInfo[] geneticInfos = new GeneticInfo[inputClients.length];

        for (int i = 0; i < geneticInfos.length; i++){
            geneticInfos[i] = inputClients[i].getGenom();
        }

        return geneticInfos;
    }

    private GeneticInfo[] createNewGenePool(GeneticInfo[] elites, int sizeOfPool){
        GeneticInfo[] genePool = new GeneticInfo[sizeOfPool];

        for(int i = 0; i < numOfElite; i++){
            genePool[i] = elites[i];
        }

        for(int i = numOfElite; i < sizeOfPool; i++){
            int parent1 = AuxilaryTools.getRandomNumber(0,numOfElite);
            int parent2 = AuxilaryTools.getRandomNumberExcept(0, numOfElite, parent1);

            GeneticInfo geneticInfo1 = elite[parent1].getGenom();
            GeneticInfo geneticInfo2 = elite[parent2].getGenom();

            GeneticInfo hybridGenetic = GenomHybridizer.uniformCrossover(geneticInfo1, geneticInfo2);

            genePool[i] = hybridGenetic;
        }

        return genePool;
    }

    private void mutateGenePool(GeneticInfo[] inputGenes){
        for (int i = 0; i < inputGenes.length; i++){
            genomMutator.mutate(inputGenes[i]);
        }
    }

    private Client[] assembleNewGeneration(GeneticInfo[] finalGenes){
        Client[] clients = new Client[finalGenes.length];

        for (int i = 0; i < clients.length; i++){
            clients[i] = new Client(finalGenes[i]);
        }
        return clients;
    }


    public void saveGenomOfElites(int numOfElitesPrinted){
        if(numOfElitesPrinted > numOfElite) return;

        for (int i = 0; i < numOfElitesPrinted; i++){
            GenomProcessor.saveGenom(elite[i].getGenom(), FolderHolder.customFolder.getAbsolutePath() + "/genom" + elite[i].id + "-" + (int)elite[i].fitnessScore);
            //System.out.println(FolderHolder.customFolder.getAbsolutePath() + "/genom" + elite[i].id + "-" + (int)elite[i].fitnessScore);
        }
    }

    public void setNumOfElite(int numOfElite) {
        this.numOfElite = numOfElite;
    }
}