package es.udc.redes.tutorial.info;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import static java.nio.file.Files.*;

public class Info {

    public static void main(String[] args) throws IOException {

        Path filePath = Path.of(args[0]); // Extract path from arguments

        String aux = filePath.getFileName().toString(); // Get file name from the path
        String[] splitedName = aux.split("\\."); // Split the path to get name and extension of the file
        String fileName = "null";
        String extension = "null" ;

        long size = size(filePath); // Save the size of the file
        FileTime lastModifiedTime = getLastModifiedTime(filePath); // Save the Last modified time of the file

        Path absolutePath = Paths.get(filePath.toUri()); // Get the absolute path

        // Find the file type
        String fileType = "null";

        if (isRegularFile(filePath)) {
            fileType = "Regular File";
            fileName = splitedName[0]; // Save the file name
            extension = "." + splitedName[1]; // Save the file extension
        } else if (isDirectory(filePath)) {
            fileType = "Directory";
            fileName = String.valueOf(filePath.getName(filePath.getNameCount()-1));
        } else if (isSymbolicLink(filePath)) {
            fileType = "Symbolic Link";
        } else if (isHidden(filePath)) {
            fileType = "Hidden File";
        }

        /* Print all information of the file */
        System.out.println("File Name: " + fileName);
        System.out.println("Size: " + size + " bytes");
        System.out.println("Last Modified Time: " + lastModifiedTime);
        System.out.println("Extension: " + extension);
        System.out.println("Type: " + fileType);
        System.out.println("Absolute Path: " + absolutePath);

    }
    
}
