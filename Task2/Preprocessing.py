'''
This file includes data cleaning and preprocessing

'''
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split

def read_data(file_path):
	return pd.read_csv(file_path)

def split_data(data):
	return train_test_split(data, train_size = 0.7)

data = read_data("./ReviewData.csv")
train_data, test_data = split_data(data)
print len(data) , len(train_data), len(test_data) , type(train_data)

