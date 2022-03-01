package com.stypox.mastercom_workbook.util;

import java.util.ArrayList;
import java.util.List;

// copied from dicio-android
public class StringUtils {

    /**
     * Returns the dynamic programming memory obtained when calculating the Levenshtein distance.
     * The solution lies at {@code memory[a.length()][b.length()]}. This memory can be used to find
     * the set of actions (insertion, deletion or substitution) to be done on the two strings to
     * turn one into the other.
     * @param a the first string
     * @param b the second string
     * @return the memory of size {@code (a.length()+1) x (b.length()+1)}
     */
    private static int[][] levenshteinDistanceMemory(final String a, final String b) {
        // memory already filled with zeros, as it's the default value for int
        final int[][] memory = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); ++i) {
            memory[i][0] = i;
        }
        for (int j = 0; j <= b.length(); ++j) {
            memory[0][j] = j;
        }

        for (int i = 0; i < a.length(); ++i) {
            for (int j = 0; j < b.length(); ++j) {
                final int substitutionCost = Character.toLowerCase(a.codePointAt(i))
                        == Character.toLowerCase(b.codePointAt(j)) ? 0 : 1;
                memory[i+1][j+1] = Math.min(Math.min(memory[i][j+1] + 1, memory[i+1][j] + 1),
                        memory[i][j] + substitutionCost);
            }
        }

        return memory;
    }

    private static class LevenshteinMemoryPos {
        final int i;
        final int j;
        LevenshteinMemoryPos(final int i, final int j) {
            this.i = i;
            this.j = j;
        }
    }

    private static List<LevenshteinMemoryPos> pathInLevenshteinMemory(
            final String a, final String b, final int[][] memory) {
        // follow the path from bottom right (score==distance) to top left (where score==0)
        final List<LevenshteinMemoryPos> positions = new ArrayList<>();
        int i = a.length() - 1, j = b.length() - 1;
        while (i >= 0 && j >= 0) {
            positions.add(new LevenshteinMemoryPos(i, j));
            if (memory[i+1][j+1] == memory[i][j+1] + 1) {
                // the path goes up
                --i;
            } else if (memory[i+1][j+1] == memory[i+1][j] + 1) {
                // the path goes left
                --j;
            } else  {
                // the path goes up-left diagonally (surely either
                // memory[i+1][j+1] == memory[i][j] or memory[i+1][j+1] == memory[i][j] + 1)
                --i;
                --j;
            }
        }
        return positions;
    }

    /**
     * Calculates a custom string distance between the two provided strings. Internally calculates
     * the dynamic programming memory of the
     * <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance</a>, then
     * follows the path chosen by the dynamic programming algorithm and draws some statistics about
     * the total number of matched character and the maximum number of roughly subsequent characters
     * matched. The result is a combination of the latter statistics and the actual Levenshtein
     * distance.
     * @param a the first string
     * @param b the second string
     * @return the custom string distance between the two cleaned strings as described above, lower
     *         is better, values can be lower than 0, values are always less than or equal to the
     *         levenshtein distance between the two strings
     */
    public static int customStringDistance(final String a, final String b) {
        final int[][] memory = levenshteinDistanceMemory(a, b);

        int matchingCharCount = 0;
        int subsequentChars = 0;
        int maxSubsequentChars = 0;
        for (final LevenshteinMemoryPos pos : pathInLevenshteinMemory(a, b, memory)) {
            if (Character.toLowerCase(a.codePointAt(pos.i))
                    == Character.toLowerCase(b.codePointAt(pos.j))) {
                ++matchingCharCount;
                ++subsequentChars;
                maxSubsequentChars = Math.max(maxSubsequentChars, subsequentChars);
            } else {
                subsequentChars = Math.max(0, subsequentChars - 1);
            }
        }

        return memory[a.length()][b.length()] - maxSubsequentChars - matchingCharCount;
    }
}
