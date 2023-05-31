##
## Makefile
##
## Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
## copy or distribute without permission. Written by Pieter Robberechts, 2023
## 

.PHONY: clean bf_small lsh_small lsh_full

# Experiment parameters ######################################################

# Dataset directory
DATAFOLDER=../tweets.tsv

# Ouptut directory
OUTPUT=../experiments/numBands_RC/numBands_18.tsv

# Experiment parameters
THRESHOLD=0.9
# NB_TWEETS=8870959
NB_TWEETS=5000000
SHINGLE_LENGTH=3
NB_SHINGLES=2000
NB_HASHES=48
NB_BANDS=1
NB_BUCKETS=1000000000

# Compilation  ###############################################################

## Locate directories
class_d=bin
source_d=src

# Compilation stuff
JAVAC=javac
JFLAGS=-g -d $(class_d) -cp $(class_d) -Xlint:all

clean:
	rm -rf $(class_d)/*

$(class_d)/LSHHashTable.class: $(source_d)/LSHHashTable.java
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/MurmurHash.class: $(source_d)/MurmurHash.java
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/Primes.class: $(source_d)/Primes.java
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/SimilarPair.class: $(source_d)/SimilarPair.java
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/Shingler.class: $(source_d)/Shingler.java $(class_d)/MurmurHash.class
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/Reader.class: $(source_d)/Reader.java $(class_d)/Shingler.class
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/TwitterReader.class: $(source_d)/TwitterReader.java $(class_d)/Reader.class
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/SimilaritySearcher.class: $(source_d)/SimilaritySearcher.java $(class_d)/Reader.class $(class_d)/SimilarPair.class
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/BruteForceSearch.class: $(source_d)/BruteForceSearch.java $(class_d)/SimilaritySearcher.class
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/Minhash.class: $(source_d)/Minhash.java
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/LSH.class: $(source_d)/LSH.java $(class_d)/SimilaritySearcher.class $(class_d)/Primes.class $(class_d)/Minhash.class $(class_d)/LSHHashTable.class 
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/LSHOptimized.class: $(source_d)/LSHOptimized.java $(class_d)/SimilaritySearcher.class $(class_d)/Primes.class $(class_d)/Minhash.class $(class_d)/LSHHashTable.class 
	@$(JAVAC) $(JFLAGS) $<

$(class_d)/Runner.class: $(source_d)/Runner.java $(class_d)/TwitterReader.class $(class_d)/BruteForceSearch.class $(class_d)/LSH.class $(class_d)/LSHOptimized.class
	@$(JAVAC) $(JFLAGS) $<

# Experiments ################################################################

bf_small: $(class_d)/Runner.class
	@echo "Testing BF on subset of data"
	time java -cp .:$(class_d) -Xmx2g Runner \
		-method bf \
		-maxTweets 500000 \
		-dataFile ${DATAFOLDER} \
		-outputFile ${OUTPUT} \
		-threshold 0.9 \
		-shingleLength ${SHINGLE_LENGTH} \
		-numShingles ${NB_SHINGLES}

lsh_small: $(class_d)/Runner.class
	@echo "Testing LSH on subset of data"
	time java -cp .:$(class_d) -Xmx2g Runner \
		-method lsh \
		-maxTweets 200000 \
		-dataFile ${DATAFOLDER} \
		-outputFile ${OUTPUT} \
		-threshold 0.9 \
		-shingleLength ${SHINGLE_LENGTH} \
		-numShingles ${NB_SHINGLES} \
		-numHashes ${NB_HASHES} \
		-numBands ${NB_BANDS}
		-numBuckets ${NB_BUCKETS}


lsh_full: $(class_d)/Runner.class
	@echo "Running LSH on full dataset"
	time java -cp .:$(class_d) -Xmx2g Runner \
		-method lsh \
		-maxTweets ${NB_TWEETS} \
		-dataFile ${DATAFOLDER} \
		-outputFile ${OUTPUT} \
		-threshold ${THRESHOLD} \
		-shingleLength ${SHINGLE_LENGTH} \
		-numShingles ${NB_SHINGLES} \
		-numHashes ${NB_HASHES} \
		-numBands ${NB_BANDS} \
		-numBuckets ${NB_BUCKETS}
