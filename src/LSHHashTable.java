import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a hash table used in Locality Sensitive Hashing (LSH) for efficient document similarity search.
 * The hash table stores document IDs in buckets based on their corresponding hash values.
 */
public class LSHHashTable {
    private Map<Integer, Set<Integer>> buckets;

    /**
     * Constructs an empty LSH hash table.
     */
    public LSHHashTable() {
        buckets = new HashMap<>();
    }

    /**
     * Inserts a document ID into the hash table with the specified bucket index.
     * If the bucket index already exists, the document ID is added to the existing set of document IDs.
     * If the bucket index does not exist, a new set is created and the document ID is added to it.
     *
     * @param bucketIndex the index of the bucket in which to insert the document ID
     * @param documentId the ID of the document to insert
     */
    public void insert(int bucketIndex, int documentId) {
        if (buckets.containsKey(bucketIndex)) {
            Set<Integer> documentIds = buckets.get(bucketIndex);
            documentIds.add(documentId);
        } else {
            Set<Integer> documentIds = new HashSet<>();
            documentIds.add(documentId);
            buckets.put(bucketIndex, documentIds);
        }
    }

    /**
     * Retrieves the set of document IDs stored in the bucket with the specified index.
     *
     * @param bucketIndex the index of the bucket
     * @return the set of document IDs in the bucket, or null if the bucket does not exist
     */
    public Set<Integer> getDocumentsInBucket(int bucketIndex) {
        return buckets.get(bucketIndex);
    }

    /**
     * Retrieves all the buckets in the LSH hash table.
     *
     * @return a collection of sets, where each set represents a bucket and contains the document IDs in that bucket
     */
    public Collection<Set<Integer>> getAllBuckets(){
        return buckets.values();
    }
}


