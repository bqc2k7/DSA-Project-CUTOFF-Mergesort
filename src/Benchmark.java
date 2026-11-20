import java.io.*;
import java.util.*;

public class Benchmark {

    public static final int[] CUTOFFS = {1, 2, 4, 6, 7, 8, 10, 12, 14, 16, 20, 24, 28, 32, 40};
    public static final int[] SIZES = {10_000, 100_000, 1_000_000};
    
    public static final int REPS_10K = 30;
    public static final int REPS_100K = 15;
    public static final int REPS_1M = 5;

    public static void main(String[] args) {
        Random rng = new Random(42);
        Map<Integer, Double[]> datasets = new HashMap<>();
        for (int n : SIZES) {
            Double[] arr = new Double[n];
            for (int i = 0; i < n; i++) {
                arr[i] = rng.nextDouble();
            }
            datasets.put(n, arr);
        }

        Double[] warmupArr = new Double[10_000];
        for (int i = 0; i < 5; i++) {
            System.arraycopy(datasets.get(10_000), 0, warmupArr, 0, 10_000);
            MergeSort.CUTOFF = 7;
            MergeSort.sort(warmupArr);
        }

        Map<Integer, Map<Integer, Double>> results = new LinkedHashMap<>();

        for (int n : SIZES) {
            int reps = (n == 10_000) ? REPS_10K : (n == 100_000 ? REPS_100K : REPS_1M);
            System.out.printf("Running benchmark for N = %,d (%d reps)...%n", n, reps);

            Double[] orig = datasets.get(n);
            Double[] work = new Double[n];
            Map<Integer, Double> timeByCutoff = new LinkedHashMap<>();

            for (int c : CUTOFFS) {
                double[] times = new double[reps];

                for (int r = 0; r < reps; r++) {
                    System.arraycopy(orig, 0, work, 0, n);
                    MergeSort.CUTOFF = c;

                    long startTime = System.nanoTime();
                    MergeSort.sort(work);
                    long endTime = System.nanoTime();

                    if (!MergeSort.isSorted(work)) {
                        throw new RuntimeException("Sort failed at CUTOFF = " + c);
                    }

                    times[r] = (endTime - startTime) / 1_000_000.0;
                }

                Arrays.sort(times);
                double medianTime = times[reps / 2];
                timeByCutoff.put(c, medianTime);
            }
            results.put(n, timeByCutoff);
        }

        System.out.println();
        System.out.printf("%-8s | %-14s | %-14s | %-14s%n", "CUTOFF", "N = 10,000", "N = 100,000", "N = 1,000,000");
        System.out.println("------------------------------------------------------------------");

        for (int c : CUTOFFS) {
            double t10k = results.get(10_000).get(c);
            double t100k = results.get(100_000).get(c);
            double t1m = results.get(1_000_000).get(c);

            System.out.printf("%-8d | %10.3f ms | %10.3f ms | %10.3f ms%n", c, t10k, t100k, t1m);
        }

        saveSummaryFile(results);
    }

    private static void saveSummaryFile(Map<Integer, Map<Integer, Double>> results) {
        File resDir = new File("results");
        if (!resDir.exists()) resDir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter("results/ket_qua_do.txt"))) {
            pw.printf("%-8s | %-14s | %-14s | %-14s%n", "CUTOFF", "N = 10,000", "N = 100,000", "N = 1,000,000");
            pw.println("------------------------------------------------------------------");
            for (int c : CUTOFFS) {
                pw.printf("%-8d | %10.3f ms | %10.3f ms | %10.3f ms%n",
                        c,
                        results.get(10_000).get(c),
                        results.get(100_000).get(c),
                        results.get(1_000_000).get(c));
            }
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
        }
    }
}
