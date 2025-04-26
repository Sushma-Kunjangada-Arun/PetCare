/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petcare;

/**
 *
 * @author sushmaka
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    // Reads a file and returns a list of lines
    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) {
            // Create empty file if not found
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create file: " + filename);
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + filename);
            e.printStackTrace();
        }

        return lines;
    }

    // Writes a list of lines to a file (overwrites)
    public static void writeFile(String filename, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to write file: " + filename);
            e.printStackTrace();
        }
    }
}

