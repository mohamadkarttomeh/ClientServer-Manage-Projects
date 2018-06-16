package Different;

import java.util.List;

public class StringsComparator {

    private final List<String> left;
    private final List<String> right;

    /**
     * Temporary variables.
     */
    private final int[] vDown;
    private final int[] vUp;

    public StringsComparator(List<String> left, List<String> right) {
        this.left = left;
        this.right = right;

        final int size = left.size() + right.size() + 2;
        vDown = new int[size];
        vUp = new int[size];
    }

    public Diff<String> getScript() {
        final Diff<String> script = new Diff<>();
        buildScript(0, left.size(), 0, right.size(), script);
        return script;
    }

    /**
     * Build an edit script.
     *
     * @param start1 the begin of the first sequence to be compared
     * @param end1 the end of the first sequence to be compared
     * @param start2 the begin of the second sequence to be compared
     * @param end2 the end of the second sequence to be compared
     * @param script the edited script
     */
    private void buildScript(final int start1, final int end1, final int start2, final int end2, final Diff<String> script) {
        final Snake middle = getMiddleSnake(start1, end1, start2, end2);

        if (middle == null || middle.getStart() == end1 && middle.getDiag() == end1 - end2 || middle.getEnd() == start1 && middle.getDiag() == start1 - start2) {

            int i = start1;
            int j = start2;
            while (i < end1 || j < end2) {
                if (i < end1 && j < end2 && left.get(i).equals(right.get(j))) {
                    script.append(new NoChange<>(left.get(i)));
                    ++i;
                    ++j;
                } else {
                    if (end1 - start1 > end2 - start2) {
                        script.append(new Delete<>(left.get(i)));
                        ++i;
                    } else {
                        script.append(new Insert<>(right.get(j)));
                        ++j;
                    }
                }
            }

        } else {

            buildScript(start1, middle.getStart(), start2, middle.getStart() - middle.getDiag(), script);
            for (int i = middle.getStart(); i < middle.getEnd(); ++i) {
                script.append(new NoChange<String>(left.get(i)));
            }
            buildScript(middle.getEnd(), end1, middle.getEnd() - middle.getDiag(), end2, script);
        }
    }

    private Snake getMiddleSnake(int start1, int end1, int start2, int end2) {
        // Myers Algorithm
        // Initialisations
        final int m = end1 - start1;
        final int n = end2 - start2;
        if (m == 0 || n == 0) {
            return null;
        }

        final int delta = m - n;
        final int sum = n + m;
        final int offset = (sum % 2 == 0 ? sum : sum + 1) / 2;
        vDown[1 + offset] = start1;
        vUp[1 + offset] = end1 + 1;

        for (int d = 0; d <= offset; ++d) {
            // Down
            for (int k = -d; k <= d; k += 2) {
                // First step

                final int i = k + offset;
                if (k == -d || k != d && vDown[i - 1] < vDown[i + 1]) {
                    vDown[i] = vDown[i + 1];
                } else {
                    vDown[i] = vDown[i - 1] + 1;
                }

                int x = vDown[i];
                int y = x - start1 + start2 - k;

                while (x < end1 && y < end2 && left.get(x).equals(right.get(y))) {
                    vDown[i] = ++x;
                    ++y;
                }
                // Second step
                if (delta % 2 != 0 && delta - d <= k && k <= delta + d) {
                    if (vUp[i - delta] <= vDown[i]) {
                        return buildSnake(vUp[i - delta], k + start1 - start2, end1, end2);
                    }
                }
            }

            // Up
            for (int k = delta - d; k <= delta + d; k += 2) {
                // First step
                final int i = k + offset - delta;
                if (k == delta - d || k != delta + d && vUp[i + 1] <= vUp[i - 1]) {
                    vUp[i] = vUp[i + 1] - 1;
                } else {
                    vUp[i] = vUp[i - 1];
                }

                int x = vUp[i] - 1;
                int y = x - start1 + start2 - k;
                while (x >= start1 && y >= start2 && left.get(x).equals(right.get(y))) {
                    vUp[i] = x--;
                    y--;
                }
                // Second step
                if (delta % 2 == 0 && -d <= k && k <= d) {
                    if (vUp[i] <= vDown[i + delta]) {
                        return buildSnake(vUp[i], k + start1 - start2, end1, end2);
                    }
                }
            }
        }

        // this should not happen
        throw new RuntimeException("Internal Error");
    }

    /**
     * Build a snake.
     *
     * @param start the value of the start of the snake
     * @param diag the value of the diagonal of the snake
     * @param end1 the value of the end of the first sequence to be compared
     * @param end2 the value of the end of the second sequence to be compared
     * @return the snake built
     */
    private Snake buildSnake(final int start, final int diag, final int end1, final int end2) {
        int end = start;
        while (end - diag < end2 && end < end1 && left.get(end).equals(right.get(end - diag))) {
            ++end;
        }
        return new Snake(start, end, diag);
    }

    /**
     * This class is a simple placeholder to hold the end part of a path under
     * construction in a {@link StringsComparator StringsComparator}.
     */
    private static class Snake {

        private final int start;

        private final int end;

        /**
         * Diagonal number.
         */
        private final int diag;

        public Snake(final int start, final int end, final int diag) {
            this.start = start;
            this.end = end;
            this.diag = diag;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public int getDiag() {
            return diag;
        }
    }

}
