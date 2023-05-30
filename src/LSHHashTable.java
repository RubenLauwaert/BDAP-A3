import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LSHHashTable {
    private Map<Integer, Set<Integer>> buckets;

    public LSHHashTable() {
        buckets = new HashMap<>();
    }

    public void insert(int bucketIndex, int documentId) {
        // Check if the bucket index already exists in the map
        if (buckets.containsKey(bucketIndex)) {
            // Retrieve the set of document IDs for the bucket index and add the new document ID
            Set<Integer> documentIds = buckets.get(bucketIndex);
            documentIds.add(documentId);
        } else {
            // Create a new set and add the document ID
            Set<Integer> documentIds = new HashSet<>();
            documentIds.add(documentId);
            // Insert the new set into the map with the bucket index
            buckets.put(bucketIndex, documentIds);
        }
    }

    public Set<Integer> getDocumentsInBucket(int bucketIndex) {
        // Retrieve the set of document IDs for the given bucket index
        return buckets.get(bucketIndex);
    }

    public Collection<Set<Integer>> getAllBuckets(){
        return buckets.values();
    }

}

