/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
 * copy or distribute without permission. Written by Pieter Robberechts, 2023
 */

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * Implementation of minhash and locality sensitive hashing (LSH) to find
 * similar objects.
 *
 * The LSH should first construct a signature matrix. Based on this, LSH is
 * performed resulting in a mapping of band ids to hash tables (stored in
 * bandToBuckets). From this bandsToBuckets mapping, the most similar items
 * should then be retrieved.
 *
 */
public class LSH extends SimilaritySearcher {

    int numHashes;
    int numBands;
    int numBuckets;
    int seed;

    /**
     * Construct an LSH similarity searcher.
     *
     * @param reader the document reader
     * @param numHashes number of hashes to use to construct the signature matrix
     * @param numBands number of bands to use during locality sensitive hashing
     * @param numBuckets number of buckets to use during locality sensitive hashing
     * @param seed should be used to generate any random numbers needed
     */
    public LSH(Reader reader, int numHashes, int numBands, int numBuckets, int seed){
        super(reader);

        this.numHashes = numHashes;
        this.numBands = numBands;
        this.numBuckets = numBuckets;
        this.seed = seed;
     
    }


    /**
     * Returns the pairs with similarity above threshold (approximate).
     */
    @Override
    public Set<SimilarPair> getSimilarPairsAboveThreshold(double threshold) {
        Set<SimilarPair> similarPairsAboveThreshold = new HashSet<SimilarPair>();

        int[][]  hashTable = Minhash
            .constructHashTable(this.numHashes, this.reader.getNumShingles(), this.seed);
        
        
        int[][] signatureMatrix = Minhash.constructSignatureMatrix(this.reader, hashTable);

        // Computing Similarities
        int bandLength = this.numHashes / this.numBands;

        for(int obj1=0 ; obj1 < signatureMatrix.length ; obj1++){
                int[][] subSignaturesObj1 = splitSignatureIntoBands(signatureMatrix[obj1], this.numBands);
            for(int obj2=0 ; obj2 < obj1 ; obj2++){
                int[][] subSignaturesObj2 = splitSignatureIntoBands(signatureMatrix[obj2], this.numBands);
                // Compare bands (Seperate bucket for each band)
                for(int b=0 ; b < this.numBands ; b++){
                    int[] subSignatureObj1 = subSignaturesObj1[b];
                    int[] subSignatureObj2 = subSignaturesObj2[b];
                    int hashSubSignatureObj1 = MurmurHash.hash32(intArrayToByteArray(subSignatureObj1), bandLength , this.seed);
                    int hashSubSignatureObj2 = MurmurHash.hash32(intArrayToByteArray(subSignatureObj2), bandLength , this.seed);
                    int bucketSubSignatureObj1 = hashSubSignatureObj1 % (this.numBuckets / 2);
                    int bucketSubSignatureObj2 = hashSubSignatureObj2 % (this.numBuckets / 2);
                    if(bucketSubSignatureObj1 == bucketSubSignatureObj2){
                        // Candidate Pair
                        double sim = jaccardSimilarity(arrayToSet(signatureMatrix[obj1]), arrayToSet(signatureMatrix[obj2]));
                        if(sim > threshold){
                            similarPairsAboveThreshold.add(new SimilarPair(reader.getExternalId(obj1), reader.getExternalId(obj2), sim));

                        }

                        break;
                    }
                }
             
            }
        }


        return similarPairsAboveThreshold;
    }

    public static int[][] splitSignatureIntoBands(int[] signature, int numBands){

        int numRows = signature.length / numBands;
        int[][] subSignatures = new int[numBands][numRows];
        
        for(int i=0 ; i < signature.length ; i++){
            int bandIndex = i / numRows;
            int subRowIndex = i % numRows;
            subSignatures[bandIndex][subRowIndex] = signature[i];
        }

        return subSignatures;
    }

    public Set<Integer> arrayToSet(int[] arr) {
        Set<Integer> set = new HashSet<>();
        for (int i : arr) {
            set.add(i);
        }
        return set;
    }


    public static byte[] intArrayToByteArray(int[] intArray) {
        ByteBuffer buffer = ByteBuffer.allocate(intArray.length * Integer.BYTES);
        for (int i : intArray) {
            buffer.putInt(i);
        }
        return buffer.array();
    }

    public static void printArray(int[] arr) {
        System.out.print("[ ");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i != arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(" ]");
    }

}
