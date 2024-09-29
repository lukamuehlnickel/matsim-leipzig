package org.matsim.analysis;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.zip.GZIPInputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

    public class PrintOutLinkLenght {

        public static void main(String[] args) {
            // Path to your compressed input XML file
            String gzippedXmlFile = "./input/v1.3/leipzig-v1.3-network-with-pt.xml.gz";
            // Path to your output CSV file
            String csvFile = "./output/links_lengths.csv";

            try {
                // Decompress the GZIP file
                FileInputStream fis = new FileInputStream(gzippedXmlFile);
                GZIPInputStream gzipInputStream = new GZIPInputStream(fis);
                InputStreamReader reader = new InputStreamReader(gzipInputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(reader);

                // Parse the decompressed XML content
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(bufferedReader));

                // Normalize the XML structure
                doc.getDocumentElement().normalize();

                // Get all the link elements
                NodeList linkList = doc.getElementsByTagName("link");

                // Create a FileWriter and BufferedWriter to write the CSV file
                FileWriter fileWriter = new FileWriter(csvFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // Write the CSV header
                bufferedWriter.write("LinkID,Length");
                bufferedWriter.newLine();

                // Loop through all the link nodes
                for (int i = 0; i < linkList.getLength(); i++) {
                    Node linkNode = linkList.item(i);

                    if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element linkElement = (Element) linkNode;

                        // Get the link id and length attributes
                        String linkId = linkElement.getAttribute("id");
                        String length = linkElement.getAttribute("length");

                        // Write the data to the CSV file
                        if (linkId != null && !linkId.isEmpty() && length != null && !length.isEmpty()) {
                            bufferedWriter.write(linkId + "," + length);
                            bufferedWriter.newLine();
                        }
                    }
                }

                // Close the BufferedWriter and FileWriter
                bufferedWriter.close();
                fileWriter.close();
                bufferedReader.close();
                gzipInputStream.close();
                fis.close();

                System.out.println("CSV file created successfully!");

            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }