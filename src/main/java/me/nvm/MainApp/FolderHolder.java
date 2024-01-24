package me.nvm.MainApp;

import java.io.File;
import java.io.IOException;

public class FolderHolder {
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final File USER_DOCUMENTS = new File(System.getProperty("user.home") + "/Documents");
    public static final File DEFAULT_FOLDER = new File(USER_DOCUMENTS.getAbsolutePath() + "/GenomFolder");

    public static File customFolder = new File(DEFAULT_FOLDER.getAbsolutePath());


    public static File autoInitFile(){
        File file = new File(USER_DOCUMENTS.getAbsolutePath() + "/GenomFolder");
        if(checkIfDirExists(file.getName())) return file;
        else {
                file.mkdir();
        }
        return file;
    }

    public static boolean checkIfDirExists(String name){
        File dir = USER_DOCUMENTS;

        File[] subdirectories = dir.listFiles(File::isDirectory);

        for (File subdirectory : subdirectories) {
            if (subdirectory.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}