package me.nvm.MainApp;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class AuxilaryTools {
    static Random random = new Random();

    public static void randomize2DArray(double[][] array, double minValue, double maxValue) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = getRandomDouble(minValue, maxValue);
            }
        }
    }

    public static void randomize1DArray(double[]array, double minValue, double maxValue) {
        for (int i = 0; i < array.length; i++) {
            array[i]= getRandomDouble(minValue, maxValue);
        }
    }

    public static void init2DArrayWith1(double[][] array){
        for (int x = 0; x < array.length; x++){
            for (int y = 0; y < array[x].length; y++){
                array[x][y] = 1;
            }
        }
    }

    public static double getRandomDouble(double min, double max) {
        return random.nextDouble(min, max);
        //return 1;
    }

    public static int getRandomNumber(int min, int max) {
        return random.nextInt(min, max);
    }

    public static double getRandomGauss(){
        return random.nextGaussian();
    }

    public static int getRandomNumberExcept(int min, int max, int taken) {
        int i = getRandomNumber(min,max);
        while (i == taken) {
            i = getRandomNumber(min,max);
        }
        return i;
    }
//https://www.codecademy.com/article/normalization
    public static double normalizeValue(double inputValue, double minValue, double maxValue){
        double normalised = (inputValue - minValue)/(maxValue - minValue);

        return normalised;
    }

    public static ArrayList<String> getFilenamesInDirectory(String directoryPath) {
        ArrayList<String> fileNamesList = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    fileNamesList.add(path.getFileName().toString());
                    System.out.println(path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNamesList;
    }

    public static <T extends Number> boolean isPositive(T number){
        boolean isOk = true;
        if(number == null || number.doubleValue() <= 0) return false;
        return isOk;
    }

    public static <T extends Number> boolean isNegative(T number){
        boolean isOk = true;
        if(number == null || number.doubleValue() >= 0) return false;
        return isOk;
    }
}
