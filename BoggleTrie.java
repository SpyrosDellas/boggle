/* *****************************************************************************
 *  Name: Spyros Dellas
 *  Date: 21/09/2020
 *  Description: Helper class for BoggleSolver.java
 *
 *  Implements an optimized Trie for fast lookup
 **************************************************************************** */

import java.util.ArrayList;
import java.util.List;

public class BoggleTrie {

    private static final int UPPER_CASE_OFFSET = 65;
    private static final int ENGLISH_ALPHABET = 26;

    private Node root;

    static class Node {
        byte value;
        Node[] next;

        public Node() {
            value = 0;
            next = new Node[ENGLISH_ALPHABET];
        }
    }

    /**
     * Create a new BoggleTrie; no duplicate or null keys are allowed
     */
    public BoggleTrie() {
        this.root = null;
    }

    /**
     * Create a new BoggleTrie; no duplicate or null keys are allowed
     */
    public BoggleTrie(String[] dictionary) {
        for (String word : dictionary) {
            int length = word.length();
            if (length < 3)
                continue;
            byte value = 0;
            if (length == 3 || length == 4) {
                value = 1;
            }
            else if (length == 5) {
                value = 2;
            }
            else if (length == 6) {
                value = 3;
            }
            else if (length == 7) {
                value = 5;
            }
            else {
                value = 11;
            }
            put(word, value);
        }
    }

    /**
     * value paired with key (0 if key is absent)
     */
    public byte get(String key) {
        Node x = get(key, root, 0);
        if (x == null)
            return 0;
        return x.value;
    }

    public Node get(String key, Node x, int index) {
        if (x == null) {
            return null;
        }
        if (index == key.length()) {
            return x;
        }
        return get(key, x.next[key.charAt(index) - UPPER_CASE_OFFSET], index + 1);
    }

    /**
     * is there a value paired with key?
     */
    public boolean contains(String key) {
        return get(key) != 0;
    }

    /**
     * put key-value pair into the table
     */
    public void put(String key, byte value) {
        root = put(key, value, root, 0);
    }

    private Node put(String key, byte value, Node x, int index) {
        if (x == null)
            x = new Node();
        if (index == key.length()) {
            x.value = value;
            return x;
        }
        int nextNodeId = key.charAt(index) - UPPER_CASE_OFFSET;
        x.next[nextNodeId] = put(key, value, x.next[nextNodeId], index + 1);
        return x;
    }

    /**
     * return all the keys in the table
     */
    public Iterable<String> keys() {
        return keysWithPrefix("");
    }

    /**
     * all the keys having s as a prefix
     */
    public Iterable<String> keysWithPrefix(String s) {
        List<String> keys = new ArrayList<>();
        if (s == null)
            return keys;
        Node x = get(s, root, 0);
        collect(x, keys, new StringBuilder(s));
        return keys;
    }

    private void collect(Node x, List<String> keys, StringBuilder prefix) {
        if (x == null)
            return;
        if (x.value != 0)
            keys.add(prefix.toString());
        for (int i = 0; i < ENGLISH_ALPHABET; i++) {
            Node next = x.next[i];
            if (next == null)
                continue;
            char c = (char) (UPPER_CASE_OFFSET + i);
            collect(next, keys, prefix.append(c));
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    public Node getRoot() {
        return root;
    }

    // Unit tests
    public static void main(String[] args) {

        String[] dictionary = {
                "SET", "SHE", "SHELLS", "SHORE", "SHORES", "SELLS", "SEA",
                "SEASHORE", "SEASETS"
        };

        BoggleTrie lexicon = new BoggleTrie(dictionary);

        System.out.println("\nPut " + dictionary.length + " key-value pairs...");

        System.out.println("\nAll keys:");
        for (String word : lexicon.keys()) {
            System.out.println(word + ", " + lexicon.get(word));
        }

        System.out.println("\nKeys with prefix 'SHE':");
        for (String word : lexicon.keysWithPrefix("SHE")) {
            System.out.println(word + ", " + lexicon.get(word));
        }

        System.out.println("\nKeys with prefix 'SE':");
        for (String word : lexicon.keysWithPrefix("SE")) {
            System.out.println(word + ", " + lexicon.get(word));
        }
    }

}
