import pandas as pd
import matplotlib.pyplot as plt
import os
import linecache

print(os.getcwd())

filename = 'py/csv/graph_temp.csv'
#filename = 'csv/graph_temp.csv'

#mdata = pd.read_csv(filename)
#mdata.plot(x='Date', y='SMR', marker='o', label='SMR')
#plt.legend()
#plt.show()

dateparse = lambda x: pd.datetime.strptime(x, '%Y%m%d')
data = pd.read_csv(filename, index_col='Date', header=1)

data.index = pd.to_datetime(data.index, format='%Y%m%d')
# 折れ線グラフを出力
print(data)
print(data.index)

data.plot(marker='o', ms=3)
plt.gcf().autofmt_xdate()

#plt.plot(data['Date'], data['SMR'], marker='o', label='SMR')
#plt.plot(data.index, data['MA(5)'], alpha=0.5, marker='x', linestyle="--", label='MA(5)')
#plt.plot(data['Date'], data['REG'], alpha=1.0, c='g', linestyle="--", label='REG')
#plt.legend()
#plt.show()

#plt.fill_between(data['Date'].values, data['SMR'], data['REG'], facecolor='y',alpha=0.5)
#data.plot(x='Date', y='SGTest', alpha=1.0, linestyle="--", label='SGTest')
#plt.legend()
#plt.show()

#data = data.dropna()
#data.plot(x='Date', y='Result', alpha=1.0, marker='o', label='Result')

syaryo = linecache.getline(filename, int(1))
print(syaryo)
plt.title(syaryo.replace('Syaryo,', ''),size=10)
plt.subplots_adjust(right=0.8)
#ncol = 2
plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left', borderaxespad=0, fontsize=8)
plt.show()
