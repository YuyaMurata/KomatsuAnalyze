import pandas as pd
import matplotlib.pyplot as plt
import os
import linecache

filename = 'py/csv/graph_temp.csv'
#filename = "csv/graph_temp.csv"

dateparse = lambda x: pd.datetime.strptime(x, '%Y%m%d')
data = pd.read_csv(filename, index_col='Date', header=1)

data.index = pd.to_datetime(data.index, format='%Y%m%d')
syaryo = linecache.getline(filename, int(1))

plt.style.use('ggplot')

# 折れ線グラフを出力
print(data)

data.plot(marker='o', ms=3)
#plt.gcf().autofmt_xdate()

plt.title(syaryo.replace('Syaryo,', ''),size=10)
plt.ylabel("SMR(h)")

#plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left', borderaxespad=0, fontsize=8)
plt.show()
