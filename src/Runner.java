/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
 * copy or distribute without permission. Written by Pieter Robberechts, 2023
 */
import java.util.*;
import java.io.*;

/**
 * The Runner can be ran from the commandline to find the most similar pairs
 * of documents in a directory.
 *
 * Example command to run with brute force similarity search:
 *  java Runner -threshold 0.5 -method bf -maxTweets 100 -dataFile data -shingleLength 5
 * Example command to run with LSH similarity search:
 *  java Runner -threshold 0.5 -method lsh -maxTweets 100 -dataFile data -shingleLength 5 -numHashes 100 -numBands 20
 */
public class Runner {

    public static void main(String[] args) {

        String inputFile = "";
        String outputFile = "";
        String method = "";
        int numShingles = 1000;
        int numHashes = -1;
        int numBands = -1;
        int numBuckets = 2000;
        int seed = 1234;
        int maxTweets = -1;
        int shingleLength = -1;
        float threshold = -1;

        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
            String arg = args[i];
            if (arg.equals("-method")) {
                if (!args[i+1].equals("bf") && !args[i+1].equals("lsh")){
                    System.err.println("The search method should either be brute force (bf) or minhash and locality sensitive hashing (lsh)");
                }
                method = args[i+1];
            } else if(arg.equals("-numHashes")) {
                numHashes = Integer.parseInt(args[i+1]);
            } else if(arg.equals("-numBands")) {
                numBands = Integer.parseInt(args[i+1]);
            } else if(arg.equals("-numBuckets")) {
                numBuckets = Integer.parseInt(args[i+1]);
            } else if(arg.equals("-numShingles")) {
                numShingles = Integer.parseInt(args[i+1]);
            } else if(arg.equals("-seed")) {
                seed = Integer.parseInt(args[i+1]);
            } else if(arg.equals("-dataFile")) {
                inputFile = args[i + 1];
            } else if(arg.equals("-maxTweets")) {
                maxTweets = Integer.parseInt(args[i+1]);
            } else if(arg.equals("-shingleLength")) {
                shingleLength = Integer.parseInt(args[i+1]);
            } else if(arg.equals("-threshold")) {
                threshold = Float.parseFloat(args[i+1]);
            } else if(arg.equals("-outputFile")) {
                outputFile = args[i + 1];
            }

            i += 2;
        }

        Shingler shingler = new Shingler(shingleLength, numShingles, seed);
        Reader reader = new TwitterReader(maxTweets, shingler, inputFile);

        SimilaritySearcher searcher = null;
        if (method.equals("bf")) {
            searcher = new BruteForceSearch(reader);
        } else if(method.equals("lsh")) {
            if (numHashes == -1 || numBands == -1) {
                throw new Error("Both -numHashes and -numBands are mandatory arguments for the LSH method");
            }
            searcher = new LSH(reader, numHashes, numBands, numBuckets, seed);
        }

        long startTime = System.currentTimeMillis();
        System.out.println("Searching items more similar than " + threshold + " ... ");
        Set<SimilarPair> similarItems = searcher.getSimilarPairsAboveThreshold(threshold);
        System.out.println("done! Took " +  (System.currentTimeMillis() - startTime)/1000.0 + " seconds.");
        System.out.println("--------------");
        printPairs(similarItems, outputFile);
    }


    /**
     * Prints pairs and their similarity.
     * @param similarItems A set of similar pairs
     * @param outputFile The file to write the output to
     */
    public static void printPairs(Set<SimilarPair> similarItems, String outputFile){
        try {
            File fout = new File(outputFile);
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            List<SimilarPair> sim = new ArrayList<SimilarPair>(similarItems);
            Collections.sort(sim, Collections.reverseOrder());
            for(SimilarPair p : sim) {
                bw.write(p.getId1() + "\t" + p.getId2() + "\t" + p.getSimilarity());
                bw.newLine();
            }

            bw.close();
            System.out.println("Found " + similarItems.size() + " similar pairs, saved to '" + outputFile + "'");
            System.out.println("--------------");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
