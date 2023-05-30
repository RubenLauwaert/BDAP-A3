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
 public class LSHOptimized extends SimilaritySearcher {
 
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
     public LSHOptimized(Reader reader, int numHashes, int numBands, int numBuckets, int seed){
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
        // Final similar pairs
        Set<SimilarPair> similarPairsAboveThreshold = new HashSet<SimilarPair>();
        // Matrix of hash values (used for constructing signature matrix)
        short[][]  hashTable = Minhash
            .constructHashTableOptimized(this.numHashes, this.reader.getNumShingles(), this.seed);
        long time2 = System.currentTimeMillis();

        // Constructed signature matrix
        short[][] signatureMatrix = Minhash.constructSignatureMatrixOptimized(this.reader, hashTable);

        
        int bandSize = this.numHashes / this.numBands;
        for(int bandIndex = 0 ; bandIndex < numBands ; bandIndex++){
            System.out.println("Generating candidate pairs for band: " + bandIndex);
            // Array that stores documents in buckets (used for identifying candidate pairs)
            LSHHashTable bucketsForBand = new LSHHashTable();
            int startIndex = bandIndex * bandSize;
            int endIndex = (bandIndex + 1) * bandSize;

            for(int docInternalId = 0 ; docInternalId < signatureMatrix.length ; docInternalId++){
               
                short[] docSignature = signatureMatrix[docInternalId];
                short[] bandSigForDoc = new short[bandSize];
                int bandSigIndex = 0;

                for (int i = startIndex; i < endIndex; i++) {
                    bandSigForDoc[bandSigIndex] = docSignature[i];
                    bandSigIndex++;
                }

                int hashedBand = MurmurHash.hash32(shortArrayToByteArray(bandSigForDoc), bandSize * Short.BYTES, this.seed);
                int bucket = hashedBand % this.numBuckets;
                bucketsForBand.insert(bucket, docInternalId);
            }

            for(Set<Integer> bucketForBand : bucketsForBand.getAllBuckets()){
                // Create pairs of document IDs within the same bucket
                List<Integer> bucketList = new ArrayList<>(bucketForBand);
                int bucketSize = bucketList.size();
                for (int i = 0; i < bucketSize - 1; i++) {
                    int docId1 = bucketList.get(i);
                    for (int j = i + 1; j < bucketSize; j++) {
                        int docId2 = bucketList.get(j);
                        // Calculate Jaccard similarity of candidate pair
                        double sim = jaccardSimilarity(arrayToSet(signatureMatrix[docId1]), arrayToSet(signatureMatrix[docId2]));
                        if(sim > threshold){
                            similarPairsAboveThreshold.add(new SimilarPair(reader.getExternalId(docId1), reader.getExternalId(docId2), sim));
                        }
                    }
                }
            }


        }     
        return similarPairsAboveThreshold;
    }
 
    public static Set<Integer> arrayToSet(short[] arr) {
        Set<Integer> set = new HashSet<>();
        for (short i : arr) {
            set.add((int) i);
        }
        return set;
    }
 
 
    public static byte[] shortArrayToByteArray(short[] shortArray) {
        ByteBuffer buffer = ByteBuffer.allocate(shortArray.length * Short.BYTES);
        for (short s : shortArray) {
            buffer.putShort(s);
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
