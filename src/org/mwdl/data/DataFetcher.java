package org.mwdl.data;

import org.mwdl.webManagement.Collection;
import org.mwdl.webManagement.PartnerPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Just a bunch of random helper methods.
 * Most are used in other classes in the creation and utilization of MWDL data.
 * Though some just spit out info about the data.
 * Documented on the basis of this method names.
 * Most follow the general outline of running through each row in the partner and collection data files
 * and adding each row that meats a certain condition to an ArrayList.
 *
 * @author Jonathan Wiggins
 * @version 5/9/18
 */

public class DataFetcher {

    /**
     * Gets all of the Active Collections
     *
     * @return an ArrayList of Collections
     */
    public static ArrayList<Collection> getAllActiveCollections() {

        ArrayList<Collection> toReturn = new ArrayList<>();
        try {

            Scanner collectionData = new Scanner(new File("newCollectionData.csv"));
            ArrayList<String> toParse = new ArrayList<>();

            while (collectionData.hasNextLine()) {
                toParse.add(collectionData.nextLine());
            }


            for (String line : toParse) {
                Pattern pattern = Pattern.compile("([0-9]{4}),[Tt][Rr][Uu][Ee],(.*),(.*),(.*),(.*),(collection[0-9]{4}.*),([0-9]*),([0-9]*),(.*)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    //int collectionNumber, boolean isActive, String note, String title, String publisher, String text, String img, int height, int width, String des
                    int collectionNumber = Integer.valueOf(matcher.group(1));
                    String note = matcher.group(2);
                    String title = matcher.group(3);
                    String pub = matcher.group(4);
                    String text = matcher.group(5);
                    String img = matcher.group(6);
                    String imgHRaw = matcher.group(7);
                    String imgWRaw = matcher.group(8);
                    String des = matcher.group(9);
                    int imgH;
                    int imgW;
                    if (imgHRaw.equalsIgnoreCase(""))
                        imgH = 0;
                    else
                        imgH = Integer.valueOf(imgHRaw);

                    if (imgWRaw.equalsIgnoreCase(""))
                        imgW = 0;
                    else
                        imgW = Integer.valueOf(imgWRaw);

                    toReturn.add(new Collection(collectionNumber, true, note, title, pub, text, img, imgH, imgW, des));

                }

            }

        } catch (IOException e) {
            System.err.println("Could not access the Collection Data csv file at DataFetcher.getAllActivePartners");
            System.exit(1);
        }
        return toReturn;
    }


    /**
     * Fetches the Collection object with the given collectionNumber
     * Requires that the Collection is active
     *
     * @param collectionNumber the number of an active collection
     * @return the Collection, null if it cannot be found
     */
    public static Collection fetchCollection(int collectionNumber) {
        for (Collection element : getAllActiveCollections()) {
            if (element.collectionNumber == collectionNumber) {
                return element;
            }
        }
        return null;
    }

    public static PartnerPage fetchPartner(int partnerNumber) throws FileNotFoundException {
        Scanner partnerData = new Scanner(new File("newPartnerData.csv")).useDelimiter("\n");
        while (partnerData.hasNext()) {
            String currentDataLine = partnerData.next();
            Scanner n = new Scanner(currentDataLine).useDelimiter(",");
            String currentDataNumber = n.next();
            if (currentDataNumber.contains(String.valueOf(partnerNumber))) {
                return new PartnerPage(partnerNumber, Boolean.valueOf(n.next()), n.next(), n.next(), n.next(), n.next(), n.next(), n.next(), n.next(), n.next());
            }
        }
        return null;
    }

    public static Collection fetchFromTitle(String title) {
        for (Collection element : getAllActiveCollections()) {
            if (element.title.equals(title))
                return element;
        }
        return null;
    }


    /**
     * Gets all the active Collection From a specified Partner
     *
     * @param name the name of the partner, as will be given by Collection.publisher
     * @return an ArrayList of Collections
     */
    public static ArrayList<Collection> getAllActiveCollectionsFromPartner(String name) {
        ArrayList<Collection> toReturn = new ArrayList<>();

        for (Collection element : getAllActiveCollections()) {
            if (element.publisher.equals(name)) {
                toReturn.add(element);
            }
        }
        return toReturn;
    }


    public static ArrayList<PartnerPage> getAllActivePartners() throws FileNotFoundException {
        ArrayList<PartnerPage> list = new ArrayList<>();
        Scanner partnerData = new Scanner(new File("newPartnerData.csv"));

        partnerData.next(); //skip title line

        while (partnerData.hasNextLine()) {

            String currentDataLine = partnerData.nextLine();
            Scanner currentLine = new Scanner(currentDataLine).useDelimiter(",");
            String currentDataNumber = currentLine.next();
            String isActive = currentLine.next();
            if (Boolean.valueOf(isActive)) {
                String note = currentLine.next();
                String name = currentLine.next();
                String link = currentLine.next();
                String text = currentLine.next();
                String imgName = currentLine.next();
                String imgH = currentLine.next();
                String imgW = currentLine.next();
                String imgDes;
                try {
                    imgDes = currentLine.next();
                } catch (NoSuchElementException e) {
                    imgDes = "";
                }
                list.add(new PartnerPage(Integer.valueOf(currentDataNumber), Boolean.valueOf(isActive), note, name, link, text, imgName, imgH, imgW, imgDes));
            }

        }
        return list;
    }


    public static ArrayList<Integer> getInactiveCollectionsNumbers() throws FileNotFoundException {
        ArrayList<Integer> list = new ArrayList<>();
        Scanner partnerData = new Scanner(new File("newCollectionData.csv")).useDelimiter("\n");
        while (partnerData.hasNext()) {
            try {
                String currentDataLine = partnerData.next();
                Scanner n = new Scanner(currentDataLine).useDelimiter(",");
                String currentDataNumber = n.next();
                String isActive = n.next();
                if (!Boolean.valueOf(isActive)) {
                    list.add(Integer.valueOf(currentDataNumber));
                }
            } catch (NoSuchElementException | NumberFormatException e) {
                System.err.println("DataFetcher Error: NoSuchElementException at getInactiveCollectionsNumbers ");
            }

        }
        return list;
    }


    public static ArrayList<Integer> getInactivePartnerNumbers() throws FileNotFoundException {
        ArrayList<Integer> list = new ArrayList<>();
        Scanner partnerData = new Scanner(new File("newPartnerData.csv")).useDelimiter("\n");
        while (partnerData.hasNext()) {
            try {
                String currentDataLine = partnerData.next();
                Scanner n = new Scanner(currentDataLine).useDelimiter(",");
                String currentDataNumber = n.next();
                String isActive = n.next();
                if (!Boolean.valueOf(isActive)) {
                    list.add(Integer.valueOf(currentDataNumber));
                }
            } catch (NoSuchElementException | NumberFormatException e) {
                System.err.print("Error: Check DataFetcher");
            }

        }
        return list;
    }


    public static ArrayList<String> getInactiveCollectionLines() throws FileNotFoundException {
        ArrayList<String> toReturn = new ArrayList<>();
        Scanner collections = new Scanner(new File("newCollectionData.csv"));
        //skip the titles line
        collections.nextLine();

        while (collections.hasNextLine()) {

            String currentDataLine = collections.nextLine();
            Scanner n = new Scanner(currentDataLine).useDelimiter(",");
            String currentDataNumber = n.next();
            String isActive = n.next();
            if (!Boolean.valueOf(isActive)) {
                toReturn.add(currentDataLine);
            }
        }
        return toReturn;
    }

}