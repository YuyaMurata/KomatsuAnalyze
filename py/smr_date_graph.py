import pandas as pd
import matplotlib.pyplot as plt
import os
import matplotlib.dates as mdates

print(os.getcwd())

filename = 'py/csv/graph_temp.csv'
#filename = 'csv/graph_temp.csv'

#mdata = pd.read_csv(filename)
#mdata.plot(x='Date', y='SMR', marker='o', label='SMR')
#plt.legend()
#plt.show()

dateparse = lambda x: pd.datetime.strptime(x, '%Y%m%d')
data = pd.read_csv(filename, parse_dates=['Date'], date_parser=dateparse)
print(data.dtypes)

# 折れ線グラフを出力
data.plot(x='Date', y='SMR', alpha=1.0, marker='o', label='SMR')

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

plt.legend()
plt.show()
