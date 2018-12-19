import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np
import linecache

filename = 'py/csv/graph_temp.csv'
#filename = "csv/loadmap_heat.csv"

df = pd.read_csv(filename, header=1)
syaryo = linecache.getline(filename, int(1))

plt.style.use('ggplot')

df_pivot = pd.pivot_table(data=df, values='v',
                                  columns='x', index='y', aggfunc=np.mean)
print(df_pivot)

sns.heatmap(df_pivot, annot=True)

plt.title(syaryo)

plt.show()