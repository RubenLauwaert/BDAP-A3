/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
 * copy or distribute without permission. Written by Pieter Robberechts, 2023
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Shingler constructs the shingle representations of documents.
 *
 * It takes all substrings of length k of the document, and maps these
 * substrings to an integer value that is inserted into the documents shingle
 * set.
 */
public class Shingler {

    private int k;
    private int numShingles;
    private int seed;

    /**
     * Construct a shingler.
     * @param k number of characters in one shingle
     * @param numShingles number of shingles (i.e., hash buckets) to use
     * @param seed seed for the hash function
     */
    public Shingler(int k, int numShingles, int seed) {
        this.k = k;
        this.numShingles = numShingles;
        this.seed = seed;
    }

    /**
     * Hash a k-shingle to an integer.
     * @param shingle shingle to hash
     * @return integer that the shingle maps to
     */
    private int hashShingle(String shingle) {
        int hash = MurmurHash.hash32(shingle, this.seed);
        return Math.abs(hash) % getNumShingles();
    }

    /**
     * Get the shingle set representation of a document.
     * @param doc document that should be shingled, given as a string
     * @return set of integers being the hash maps of the shingles
     */
    public Set<Integer> shingle(String doc) {
        Set<Integer> shingled = new HashSet<Integer>();
        for (int i = 0; i < doc.length() - k + 1; i++) {
            String toHash = Character.toString(doc.charAt(i));
            for (int j = 0; j < k - 1; j++){
                toHash += Character.toString(doc.charAt(i+j+1));
            }
            shingled.add(hashShingle(toHash));
        }
        return shingled;
    }

    /**
     * Get the number of unique shingles this shingler has processed.
     * @return number of unique shingles
     */
    public int getNumShingles() {
        return this.numShingles;
    }

}
