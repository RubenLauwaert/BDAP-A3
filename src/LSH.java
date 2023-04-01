/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
 * copy or distribute without permission. Written by Pieter Robberechts, 2023
 */

import java.util.*;
import java.io.*;

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

        // THIS METHOD IS REQUIRED

        return similarPairsAboveThreshold;
    }

}
