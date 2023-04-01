/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not
 * copy or distribute without permission. Written by Pieter Robberechts, 2023
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

/**
 * Reads tweets from a file and constructs shingle representations for
 * these tweets.
 */
public class TwitterReader extends Reader {

    private String filePath;
    private BufferedReader br;
    private Scanner scanner;

    public TwitterReader(int maxDocs, Shingler shingler, String filePath) {
        super(maxDocs, shingler);

        this.filePath = filePath;
        reset();
    }

    @Override
    public Set<Integer> next() {
        this.curDoc++;

        if (this.curDoc >= this.maxDocs) {
            return null;
        }

        if (curDoc % 100000 == 0) {
            System.out.println("at doc " + curDoc);
        }

        String line = scanner.next();
        String[] cols = line.split("\t", -1);

        long tweetId = Long.parseLong(cols[1]);
        this.idToDoc.add(tweetId);

        String tweet = cols[2];
        Set<Integer> shingle = this.shingler.shingle(tweet);

        return shingle;
    }

    @Override
    public void reset() {
        try {
            this.scanner = new Scanner(new File(filePath));
            scanner.useDelimiter("\n");
            System.gc();
            this.curDoc = -1;
            this.idToDoc = new ArrayList<Long>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
