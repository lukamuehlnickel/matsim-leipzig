package org.matsim.analysis;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EmissionsFileUpdater {

    public static void main(String[] args) {
        // Path to the CSV containing link lengths
        String linksCsvFile = "./output/links_lengths.csv";
        // Path to the emissions CSV file
        String emissionsCsvFile = "D:/MATSIM-Emissions/base/emissions/emissionsPerLinkPerM.csv/";
        // Path to the updated emissions CSV file
        String updatedEmissionsCsvFile = "D:/MATSIM-Emissions/base/emissions/updated_emissions.csv";

        try {
            // Step 1: Load the CSV file with link lengths into a Map
            Map<String, String> linkLengthMap = loadLinkLengthsFromCSV(linksCsvFile);

            // Step 2: Read the emissions CSV and update it with lengths
            BufferedReader emissionsReader = new BufferedReader(new FileReader(emissionsCsvFile));
            BufferedWriter emissionsWriter = new BufferedWriter(new FileWriter(updatedEmissionsCsvFile));

            String header = emissionsReader.readLine();
            // Add the "Length" column to the emissions CSV header
            emissionsWriter.write(header + ";Length");
            emissionsWriter.newLine();

            String line;
            // Step 3: Process each row in the emissions file
            while ((line = emissionsReader.readLine()) != null) {
                String[] parts = line.split(";");
                String linkId = parts[0];  // Assuming first column is 'LinkID'

                // Check if the linkId exists in the linkLengthMap
                String length = linkLengthMap.getOrDefault(linkId, "");

                // Write the emissions row along with the length (if available)
                emissionsWriter.write(line + ";" + length);
                emissionsWriter.newLine();
            }

            // Close the readers and writers
            emissionsReader.close();
            emissionsWriter.close();

            System.out.println("Updated emissions CSV file created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load link lengths from CSV into a Map
    private static Map<String, String> loadLinkLengthsFromCSV(String csvFile) throws IOException {
        Map<String, String> linkLengthMap = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String line;

        // Skip the header row (LinkID,Length)
        reader.readLine();

        // Read each line and populate the map
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String linkId = parts[0].trim();
                String length = parts[1].trim();
                linkLengthMap.put(linkId, length);
            }
        }

        reader.close();
        return linkLengthMap;
    }
}