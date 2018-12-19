import linecache

import matplotlib.pyplot as plt
import pandas as pd

filename = 'py/csv/graph_temp.csv'
#filename = "csv/loadmap_bar.csv"

df = pd.read_csv(filename, header=1, index_col='label')
syaryo = linecache.getline(filename, int(1))

print(df)

plt.style.use('ggplot')

df.plot.bar()
plt.title(syaryo)

plt.ylabel("Hour")
plt.show()