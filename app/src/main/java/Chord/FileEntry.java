package Chord;

import java.io.File;
import java.io.Serializable;

public class FileEntry implements Serializable{

    File file;
    String fileData;
    String origin;
    String destination;

    public FileEntry() {/*
        this.file = file;
        this.fileData = fileData;*/
    }

    public FileEntry(File file, String fileData) {
        this.file = file;
        this.fileData = fileData;
    }

    public FileEntry(File file, String fileData, String origin, String destination) {
        this.file = file;
        this.fileData = fileData;
        this.origin=origin;
        this.destination=destination;
    }

    public File getFile() {
        return file;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }


    public String toString(){
        return " " + file.getName() + "\n" + fileData;
    }
}
