import java.util.Arrays;
import java.util.Random;

public class TestMergeSort {
    static final int[] CUTOFFS = {1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 16, 20, 24, 28, 32};

    static class Item implements Comparable<Item> {
        final int key, id;
        Item(int k, int i) { key = k; id = i; }
        public int compareTo(Item o) { return Integer.compare(key, o.key); }
    }

    public static void main(String[] args) {
        Random rnd = new Random(42);
        int checks = 0;

        for (int c : CUTOFFS) {
            MergeSort.CUTOFF = c;
            int[] sizes = {0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 15, 16, 17, 31, 32, 33, 100, 128, 1000};

            for (int n : sizes) {
                Double[] a = new Double[n];
                for (int i = 0; i < n; i++) a[i] = rnd.nextDouble();
                Double[] ref = a.clone();
                Arrays.sort(ref);
                MergeSort.sort(a);
                check(Arrays.equals(a, ref), "Double sort error: cutoff=" + c + ", n=" + n);

                Item[] it = new Item[n];
                for (int i = 0; i < n; i++) it[i] = new Item(rnd.nextInt(10), i);
                MergeSort.sort(it);
                for (int i = 1; i < n; i++) {
                    check(it[i - 1].key <= it[i].key, "Order error: cutoff=" + c + ", n=" + n);
                    if (it[i - 1].key == it[i].key) {
                        check(it[i - 1].id < it[i].id, "Stability error: cutoff=" + c + ", n=" + n);
                    }
                }

                Double[] s = new Double[n], r = new Double[n], e = new Double[n];
                for (int i = 0; i < n; i++) {
                    s[i] = (double) i;
                    r[i] = (double) (n - i);
                    e[i] = 5.0;
                }
                MergeSort.sort(s);
                MergeSort.sort(r);
                MergeSort.sort(e);
                check(MergeSort.isSorted(s) && MergeSort.isSorted(r) && MergeSort.isSorted(e),
                      "Special data error: cutoff=" + c + ", n=" + n);

                checks += 3;
            }
        }
        System.out.println("All tests passed (" + checks + " checks).");
    }

    static void check(boolean ok, String msg) {
        if (!ok) {
            System.err.println("FAILED: " + msg);
            System.exit(1);
        }
    }
}
