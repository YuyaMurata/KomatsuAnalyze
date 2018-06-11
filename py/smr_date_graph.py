import pandas as pd
import matplotlib.pyplot as plt
import os
import sys

print(os.getcwd())

filename = 'py/csv/graph_temp.csv'
#filename = 'csv/graph_temp.csv'

data = pd.read_csv(filename, delimiter=',')
data['Date'] = pd.to_datetime(data['Date'], format='%Y%m%d')
print(data['Date'])

# 折れ線グラフを出力
plt.plot(data['Date'], data['SMR'], marker='o', label='SMR')
plt.plot(data['Date'], data['MA(5)'], alpha=0.5, marker='x', linestyle="--", label='MA(5)')
plt.plot(data['Date'], data['REG'], alpha=0.5, linestyle="--", label='REG')
data.plot(x='Date', y='SGTest', alpha=1.0, linestyle="--", label='SGTest')

plt.legend()
plt.show()