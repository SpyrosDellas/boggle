/* *****************************************************************************
 *  Name: Spyros Dellas
 *  Date: 22-09-2020
 *  Description: Find all valid words in a Boggle game board, given a dictionary
 *
 * Uses an optimized trie on a 26 character alphabet to speed-up the search
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {

    private static final int UPPER_CASE_OFFSET = 65;
    private static final int Q = 'Q' - UPPER_CASE_OFFSET;
    private static final int U = 'U' - UPPER_CASE_OFFSET;

    private final BoggleTrie dictionary;
    private Set<String> validWords;
    private int rows;
    private int columns;

    /**
     * Initializes the data structure using the given array of strings as the dictionary.
     * We assume each word in the dictionary contains only the uppercase letters A
     * through Z
     *
     * @param dictionary the dictionary
     */
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new BoggleTrie(dictionary);
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable
     *
     * @param board a boggle board
     * @return all valid words in the given board
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        rows = board.rows();
        columns = board.cols();
        solve(board);
        return validWords;
    }

    private void solve(BoggleBoard board) {
        boolean[][] marked = new boolean[rows][columns];
        this.validWords = new HashSet<>();
        BoggleTrie.Node root = dictionary.getRoot();
        if (root == null)
            return;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                char letter = board.getLetter(row, column);
                BoggleTrie.Node node = getNextNode(letter, root);
                String prefix = getNextPrefix("", letter);
                solveFrom(board, row, column, marked, prefix, node);
            }
        }
    }

    private BoggleTrie.Node getNextNode(char letter, BoggleTrie.Node x) {
        BoggleTrie.Node nextNode;
        if (letter == 'Q') {
            nextNode = x.next[Q];
            if (nextNode != null)
                nextNode = nextNode.next[U];
        }
        else {
            nextNode = x.next[letter - UPPER_CASE_OFFSET];
        }
        return nextNode;
    }

    private String getNextPrefix(String prefix, char letter) {
        if (letter == 'Q')
            return prefix + "QU";
        else
            return prefix + letter;
    }

    private void solveFrom(BoggleBoard board, int row, int column, boolean[][] marked,
                           String prefix, BoggleTrie.Node node) {

        if (node == null || marked[row][column])
            return;
        if (node.value != 0) {
            validWords.add(prefix);
        }
        marked[row][column] = true;
        for (int i = row - 1; i <= row + 1; i++) {
            if (i < 0 || i >= rows)
                continue;
            for (int j = column - 1; j <= column + 1; j++) {
                if (j < 0 || j >= columns)
                    continue;
                if (i == row && j == column)
                    continue;
                char letter = board.getLetter(i, j);
                BoggleTrie.Node nextNode = getNextNode(letter, node);
                String nextPrefix = getNextPrefix(prefix, letter);
                solveFrom(board, i, j, marked, nextPrefix, nextNode);
            }
        }
        marked[row][column] = false;
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * Assumes the word contains only the uppercase letters A through Z.
     *
     * @param word the word to be scored
     * @return the score of the given word
     */
    public int scoreOf(String word) {
        if (word == null)
            return 0;
        return dictionary.get(word);
    }

    private static void test(String lexicon, String[] boardFiles, int[] expectedScores) {
        In in = new In(lexicon);
        String[] dict = in.readAllStrings();
        in.close();
        BoggleSolver solver = new BoggleSolver(dict);
        for (int i = 0; i < boardFiles.length; i++) {
            String boardFile = boardFiles[i];
            BoggleBoard board = new BoggleBoard(boardFile);
            int score = 0;
            for (String word : solver.getAllValidWords(board)) {
                // System.out.println(word);
                score += solver.scoreOf(word);
            }
            System.out.println("DICTIONARY = " + lexicon + ", BOARD = " + boardFile);
            System.out
                    .println("Expected score = " + expectedScores[i] + ", Actual score = " + score);
        }
    }

    // test client
    public static void main(String[] args) {
        String lexicon = "dictionary-algs4.txt";
        String[] boardFiles = { "board4x4.txt", "board-q.txt" };
        int[] expectedScores = { 33, 84 };
        test(lexicon, boardFiles, expectedScores);

        String lexicon1 = "dictionary-yawl.txt";
        String[] boardFiles1 = {
                "board-points0.txt", "board-points1.txt", "board-points2.txt",
                "board-points3.txt", "board-points4.txt", "board-points5.txt",
                "board-points100.txt", "board-points200.txt", "board-points300.txt",
                "board-points400.txt", "board-points500.txt", "board-points750.txt",
                "board-points777.txt", "board-points1000.txt", "board-points1111.txt",
                "board-points1250.txt", "board-points1500.txt", "board-points2000.txt",
                "board-points4410.txt", "board-points4527.txt", "board-points4540.txt",
                "board-points13464.txt", "board-points26539.txt"
        };
        int[] expectedScores1 = {
                0, 1, 2,
                3, 4, 5,
                100, 200, 300,
                400, 500, 750,
                777, 1000, 1111,
                1250, 1500, 2000,
                4410, 4527, 4540,
                13464, 26539
        };
        test(lexicon1, boardFiles1, expectedScores1);

        String lexicon2 = "dictionary-2letters.txt";
        String[] boardFiles2 = { "board-points4410.txt" };
        int[] expectedScores2 = { 0 };
        test(lexicon2, boardFiles2, expectedScores2);
    }


}
