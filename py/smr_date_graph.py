import pandas as pd
import matplotlib.pyplot as plt
import os
import sys

print(os.getcwd())

filename = 'py/csv/graph_temp.csv'

data = pd.read_csv(filename, delimiter=',')
data['Date'] = pd.to_datetime(data['Date'], format='%Y%m%d')
print(data['Date'])

# 折れ線グラフを出力
data.plot(x='Date', y='SMR', marker='o')
data.plot(x='Date', y='MA', marker='o', c='g')

plt.show()