import linecache

import matplotlib.pyplot as plt
import pandas as pd
import sys

file = "csv/loadmap_bar.csv"
args = sys.argv

#引数の処理
if len(args) > 1:
    file = args[1]

df = pd.read_csv(file, header=1, index_col='label')#sns.load_dataset('flights')
syaryo = linecache.getline(file, int(1))

print(df)

plt.style.use('ggplot')

df.plot.bar()
plt.title(syaryo)

plt.ylabel("Hour")
plt.show()