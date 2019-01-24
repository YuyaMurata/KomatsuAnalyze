import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np
import linecache
import sys

file = "csv/loadmap_heat.csv"
args = sys.argv

#引数の処理
if len(args) > 1:
    file = args[1]

df = pd.read_csv(file, header=1)
syaryo = linecache.getline(file, int(1))

plt.style.use('ggplot')

df_pivot = pd.pivot_table(data=df, values='v',
                                  columns='x', index='y', aggfunc=np.mean)
print(df_pivot)

sns.heatmap(df_pivot, annot=True)

plt.title(syaryo)

plt.show()