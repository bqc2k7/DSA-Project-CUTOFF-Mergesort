import matplotlib.pyplot as plt
import numpy as np

cutoffs = []
t10k = []
t100k = []
t1m = []

with open("results/ket_qua_do.txt", "r", encoding="utf-8") as f:
    for line in f:
        parts = [p.strip() for p in line.split("|")]
        if len(parts) == 4 and parts[0].isdigit():
            c = int(parts[0])
            v1 = float(parts[1].replace("ms", "").strip())
            v2 = float(parts[2].replace("ms", "").strip())
            v3 = float(parts[3].replace("ms", "").strip())
            cutoffs.append(c)
            t10k.append(v1)
            t100k.append(v2)
            t1m.append(v3)

fig, axes = plt.subplots(1, 3, figsize=(15, 5.0), dpi=160)
data_list = [(t10k, "N = 10,000 (10K)"), (t100k, "N = 100,000 (100K)"), (t1m, "N = 1,000,000 (1M)")]

for ax, (times, title) in zip(axes, data_list):
    best_idx = int(np.argmin(times))
    best_c = cutoffs[best_idx]
    best_t = times[best_idx]
    
    idx_1 = cutoffs.index(1) if 1 in cutoffs else -1
    idx_7 = cutoffs.index(7) if 7 in cutoffs else -1
    idx_10 = cutoffs.index(10) if 10 in cutoffs else -1

    ax.plot(cutoffs, times, marker="o", color="#1f77b4", lw=2, label="Time (ms)")
    if idx_1 >= 0:
        ax.scatter([1], [times[idx_1]], color="red", s=70, zorder=5, label=f"CUTOFF=1 ({times[idx_1]:.2f}ms)")
    if idx_7 >= 0:
        ax.scatter([7], [times[idx_7]], color="orange", s=70, zorder=5, label=f"CUTOFF=7 ({times[idx_7]:.2f}ms)")
    if idx_10 >= 0:
        ax.scatter([10], [times[idx_10]], color="purple", s=70, zorder=5, label=f"CUTOFF=10 ({times[idx_10]:.2f}ms)")
    ax.scatter([best_c], [best_t], color="green", s=100, marker="*", zorder=6, label=f"Best: CUTOFF={best_c} ({best_t:.2f}ms)")

    ax.set_title(title, fontsize=12, fontweight="bold")
    ax.set_xlabel("CUTOFF", fontsize=10)
    ax.set_ylabel("Execution Time (ms)", fontsize=10)
    ax.set_xlim(0, 42)
    ax.grid(True, linestyle="--", alpha=0.6)
    ax.legend(fontsize=8, loc="upper right")

plt.suptitle("Mergesort CUTOFF Optimization Benchmark (Intel Core i5-11400H)", fontsize=14, fontweight="bold", y=0.98)
plt.tight_layout(rect=[0, 0.03, 1, 0.93])
plt.savefig("figures/benchmark_i5_11400h.png", bbox_inches="tight", pad_inches=0.25)
print("Chart saved successfully to figures/benchmark_i5_11400h.png")
