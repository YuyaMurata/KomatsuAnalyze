import sys
import matplotlib.pyplot as plt
import csv
from statistics import mean, variance
from scipy import stats
import pandas as pd

# データセットの読み込み
args = sys.argv
sid = []
data = []

file = 'csv/ExportData_PC200_ALL.csv'
a = 0.01
graph = False

#引数の処理
if len(args) > 1:
    file = args[1]
    if len(args) > 2:
        a = float(args[2])
        if len(args) > 3:
            graph = True

print(file+" alpha="+str(a)+"(graph="+str(graph)+")")

csvdata = pd.read_csv(file)

# 標本平均
mean = mean(csvdata["TARGET"])

# 標本分散
variance = variance(csvdata["TARGET"])

# 異常度
anomaly_scores = []
for x in csvdata["TARGET"]:
    anomaly_score = (x - mean)**2 / variance
    anomaly_scores.append(anomaly_score)

# カイ二乗分布による1%水準の閾値
threshold = stats.chi2.interval(1-a, 1)[1]

abno = []
for s in anomaly_scores:
    if s > threshold:
        abno.append(1)
    else:
        abno.append(0)

df = pd.read_csv(file)
df['detect'] = abno
df.to_csv(file, index=None)

if graph:
    # 結果の描画
    plt.plot(anomaly_scores, "o", color = "b")
    plt.plot([0,len(df)],[threshold, threshold], 'k-', color = "r", ls = "dashed")
    plt.xlabel("Sample number")
    plt.ylabel("Anomaly score")
    plt.ylim([0,100])
    plt.show()