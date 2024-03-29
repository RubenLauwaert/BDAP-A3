/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
 * copy or distribute without permission. Written by Pieter Robberechts, 2023
 */

 import java.util.List;
import java.util.Random;
import java.util.Set;


/**
 * Class for computing MinHash signatures.
 */
public final class Minhash {

    private Minhash() {
    }

    /**
     * Construct the table of hash values needed to construct the signature matrix.
     * Position (i,j) contains the result of applying function j to row number i.
     *
     * @param numHashes  number of hashes that will be used in the signature matrix
     * @param numValues  number of unique values that occur in the object set representations (i.e. number of rows of the characteristic matrix)
     * @param seed       should be used to generate any random numbers needed
     * @return the (numValues x numHashes) matrix of hash values
     */
    public static int[][] constructHashTable(int numHashes, int numValues, int seed) {
        int[][] hashes = new int[numValues][numHashes];

        int primeN = Primes.findLeastPrimeNumber(numValues);

        // Columns of the hash table
        for (int j = 0; j < numHashes; j++) {
            // Generate random 'a' and 'b' values
            int a = new Random().nextInt();
            int b = new Random().nextInt();
            // Rows of the hash table
            for (int i = 0; i < numValues; i++) {
                // Universal hash: h_a,b(x) = ((a·x + b) mod p) mod N
                int hashValue = Math.abs(((a * i + b) % primeN) % numValues);
                hashes[i][j] = hashValue;
            }
        }

        return hashes;
    }

    /**
     * Construct the signature matrix.
     *
     * @param reader     iterator returning the set representation of objects for which the signature matrix should be constructed
     * @param hashValues (numValues x numHashes) matrix of hash values
     * @return the (numHashes x numObjects) signature matrix
     */
    public static int[][] constructSignatureMatrix(Reader reader, int[][] hashValues) {

        int numHashes = hashValues[0].length;
        int numObjects = reader.maxDocs;

        int[][] signatureMatrix = initializeSignatureMatrix(numHashes, numObjects);

        // Streamline reading of documents
        while (reader.hasNext()) {
            Set<Integer> shingleDoc = reader.next();

            // Iterate over shingle document
            for (int rowIndex : shingleDoc) {
                for (int i = 0; i < numHashes; i++) {
                    if (hashValues[rowIndex][i] < signatureMatrix[reader.curDoc][i]) {
                        signatureMatrix[reader.curDoc][i] = hashValues[rowIndex][i];
                    }
                }
            }

        }

        return signatureMatrix;
    }

    private static int[][] initializeSignatureMatrix(int numHashes, int numObjects) {
        int[][] signatureMatrixInit = new int[numObjects][numHashes];

        for (int c = 0; c < numObjects; c++) {
            for (int r = 0; r < numHashes; r++) {
                signatureMatrixInit[c][r] = Integer.MAX_VALUE;
            }
        }

        return signatureMatrixInit;
    }

    /**
     * OPTIMIZED MINHASH METHODS THAT USES SHORT DATA TYPES INSTEAD OF INT DATA TYPES
     */

    /**
     * Construct the table of hash values needed to construct the signature matrix (optimized version).
     * Position (i,j) contains the result of applying function j to row number i.
     *
     * @param numHashes number of hashes that will be used in the signature matrix
     * @param numValues number of unique values that occur in the object set representations (i.e. number of rows of the characteristic matrix)
     * @param seed      should be used to generate any random numbers needed
     * @return the (numValues x numHashes) matrix of hash values (short data type)
     */
    public static short[][] constructHashTableOptimized(int numHashes, int numValues, int seed) {
        short[][] hashes = new short[numValues][numHashes];

        int primeN = Primes.findLeastPrimeNumber(numValues);

        for (int j = 0; j < numHashes; j++) {
            int a = new Random().nextInt();
            int b = new Random().nextInt();
            for (int i = 0; i < numValues; i++) {
                int hashValue = Math.abs(((a * i + b) % primeN) % numValues);
                hashes[i][j] = (short) hashValue;
            }
        }

        return hashes;
    }

    /**
     * Construct the signature matrix (optimized version).
     *
     * @param reader     iterator returning the set representation of objects for which the signature matrix should be constructed
     * @param hashValues (numValues x numHashes) matrix of hash values (short data type)
     * @return the (numHashes x numObjects) signature matrix (short data type)
     */
    public static short[][] constructSignatureMatrixOptimized(Reader reader, short[][] hashValues) {
        int numHashes = hashValues[0].length;
        int numObjects = reader.maxDocs;

        short[][] signatureMatrix = initializeSignatureMatrixOptimized(numHashes, numObjects);

        while (reader.hasNext()) {
            Set<Integer> shingleDoc = reader.next();

            for (int rowIndex : shingleDoc) {
                for (int i = 0; i < numHashes; i++) {
                    if (hashValues[rowIndex][i] < signatureMatrix[reader.curDoc][i]) {
                        signatureMatrix[reader.curDoc][i] = hashValues[rowIndex][i];
                    }
                }
            }
        }

        return signatureMatrix;
    }

    private static short[][] initializeSignatureMatrixOptimized(int numHashes, int numObjects) {
        short[][] signatureMatrixInit = new short[numObjects][numHashes];

        for (int c = 0; c < numObjects; c++) {
            for (int r = 0; r < numHashes; r++) {
                signatureMatrixInit[c][r] = Short.MAX_VALUE;
            }
        }

        return signatureMatrixInit;
    }
}
