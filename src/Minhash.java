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
        int[][] hashes = new int[numValues][numHashes];

        int primeN= Primes.findLeastPrimeNumber(numValues);
    
        //Columns of hash table
        for(int j=0 ; j < numHashes ; j++){
            // Generate random a and b values
            int a = new Random().nextInt();
            int b = new Random().nextInt();
            // Rows of hash table
            for(int i=0 ; i < numValues ; i++){
                // universal hash : h_a,b(x)=((a·x+b) mod p) mod N
                int hashValue = Math.abs(((a * i + b) % primeN) % numValues);
                //System.out.println(hashValue);
                hashes[i][j] = hashValue;
            }
        }

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

        int numHashes = hashValues[0].length;
        int numObjects = reader.maxDocs;

        int[][] signatureMatrix = initializeSignatureMatrix(numHashes,numObjects);

        // One Pass Implementation
        List<Set<Integer>> docToShingle = reader.readAll();

        for(int docIndex=0 ; docIndex < docToShingle.size() ; docIndex++){
            // Document is represented as a set of shingles
            // Can be interpreted as a 0/1 vector where every shingle value is the index of a 1 in the vector
            Set<Integer> shingleDoc = docToShingle.get(docIndex);
            for(int rowIndex : shingleDoc){
                for(int i=0 ; i < numHashes ; i++){
                    if(hashValues[rowIndex][i] < signatureMatrix[i][docIndex]){
                        signatureMatrix[i][docIndex] = hashValues[rowIndex][i];
                    }
                }
            }
        }
        printMatrix(signatureMatrix);
        reader.reset();
        return signatureMatrix;
    }


    private static int[][] initializeSignatureMatrix(int numHashes, int numObjects){
        int[][] signatureMatrixInit = new int[numHashes][numObjects];

        for(int r=0 ; r < numHashes ; r++){
            for(int c=0 ; c < numObjects ; c++){
                signatureMatrixInit[r][c] = Integer.MAX_VALUE;
            }
        }
        
        return signatureMatrixInit;
    }

    private static void printMatrix(int[][] matrix){
        int rowAmt = matrix.length;
        int colAmt = matrix[0].length;

        for(int r=0 ; r < rowAmt ; r++){
            for(int c=0 ; c < colAmt ; c++){
                System.out.print(matrix[r][c] + " ");
            }
            System.out.println();
        }
    }

}
