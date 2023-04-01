/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
 * copy or distribute without permission. Written by Pieter Robberechts, 2023
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reads a set of documents and constructs shingle representations for
 * these documents.
 */
public abstract class Reader {

    // a shingler
    public Shingler shingler;
    // max number of docs to read
    protected int maxDocs;
    // maps each doc's internal id to its external id
    public List<Long> idToDoc = new ArrayList<Long>();
    // number of docs read
    protected int curDoc;

    /**
     * Construct a new document reader.
     * @param maxDocs maximal number of documents to read.
     * @param shingler a document shingler.
     */
    public Reader(int maxDocs, Shingler shingler) {
        this.maxDocs = maxDocs;
        this.shingler = shingler;
        this.curDoc = -1;
    }

    /**
     * Read the next document.
     * @return the shingle representation for the next document.
     */
    abstract public Set<Integer> next();

    /**
     * Reset this reader.
     */
    abstract public void reset();

    /**
     * Check whether there are more documents.
     * @return True if there are more documents; otherwise False.
     */
    public boolean hasNext() {
        return this.curDoc < this.maxDocs - 1;
    };

    /**
     * Read all maxDocs documents at once.
     * @return the mapping of the object id to its set representation.
     */
    public List<Set<Integer>> readAll() {
        reset();
        List<Set<Integer>> idToShingle = new ArrayList<Set<Integer>>();
        while (this.hasNext()){
            idToShingle.add(this.next());
        }
        return idToShingle;
    }

    /**
     * Get the number of unique shingles that were processed.
     * @return the number of unique shingles
     */
    public int getNumShingles() {
        return this.shingler.getNumShingles();
    }

    /**
     * Get the number of documents that will be processed.
     * @return the number of documents.
     */
    public int getMaxDocs() {
        return this.maxDocs;
    }

    /**
     * Map an internal id to an external id.
     */
    public long getExternalId(int id) {
        return this.idToDoc.get(id);
    }
}
