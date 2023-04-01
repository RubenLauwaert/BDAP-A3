/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
 * copy or distribute without permission. Written by Pieter Robberechts, 2023
 */

/**
 * Class for computing MinHash signatures.
 */
public final class Minhash {

    private Minhash(){
    }

    /**
     * Construct the table of hash values needed to construct the signature matrix.
     * Position (i,j) contains the result of applying function j to row number i.
     * @param numHashes number of hashes that will be used in the signature matrix
     * @param numValues number of unique values that occur in the object set representations (i.e. number of rows of the characteristic matrix)
     * @param seed should be used to generate any random numbers needed
     * @return the (numValues x numHashes) matrix of hash values
     */
    public static int[][] constructHashTable(int numHashes, int numValues, int seed) {
        int[][] hashes = null;

        // THIS METHOD IS REQUIRED

        return hashes;
    }

    /**
     * Construct the signature matrix.
     *
     * @param reader iterator returning the set represenation of objects for which the signature matrix should be constructed
     * @param hashValues (numValues x numHashes) matrix of hash values
     * @return the (numHashes x numObjects) signature matrix
     */
    public static int[][] constructSignatureMatrix(Reader reader, int[][] hashValues) {
        int[][] signatureMatrix = null;

        reader.reset();

        // THIS METHOD IS REQUIRED

        return signatureMatrix;
    }

}
