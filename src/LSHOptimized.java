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
        // Constructed signature matrix
        short[][] signatureMatrix = Minhash.constructSignatureMatrixOptimized(this.reader, hashTable);

        
        int bandSize = this.numHashes / this.numBands;

        //For each band of the signature matrix
        for(int bandIndex = 0 ; bandIndex < numBands ; bandIndex++){
            System.out.println("Generating candidate pairs for band: " + bandIndex);
            // Array that stores documents in buckets (used for identifying candidate pairs)
            LSHHashTable bucketsForBand = new LSHHashTable();
            // Indexes that specify the range of rows of the bands
            int startIndex = bandIndex * bandSize;
            int endIndex = (bandIndex + 1) * bandSize;
            // Iterate over each signature in the signature matrix
            for(int docInternalId = 0 ; docInternalId < signatureMatrix.length ; docInternalId++){
                // Document signature
                short[] docSignature = signatureMatrix[docInternalId];
                // Portion of signature that corresponds with band
                short[] bandSigForDoc = new short[bandSize];
                int bandSigIndex = 0;

                for (int i = startIndex; i < endIndex; i++) {
                    bandSigForDoc[bandSigIndex] = docSignature[i];
                    bandSigIndex++;
                }

                // Hash the band signature 
                int hashedBand = MurmurHash.hash32(shortArrayToByteArray(bandSigForDoc), bandSize * Short.BYTES, this.seed);
                // Retrieve bucket from hash of band signature
                int bucket = hashedBand % this.numBuckets;
                // Place the internal document id in the bucket hashtable
                bucketsForBand.insert(bucket, docInternalId);
            }


            // Calculate all the candidate pairs from the bucket hashtable
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
 }
