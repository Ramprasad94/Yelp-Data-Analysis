# -*- coding: utf-8 -*-
"""
Created on Sat Dec 10 19:29:50 2016

@author: Ramprasad
"""
from sklearn.feature_extraction.text import TfidfVectorizer
import os

#read in each file
newfile= open('E:/Search Final Project/Search-Project/cats/cats/Accessories.txt','r')


path = "E:/Search Final Project/Search-Project/cats/cats"

dirs = os.listdir( path )

corpus = []

for file in dirs:
    corpus.append(file)

print corpus


vect = TfidfVectorizer(sublinear_tf = True, max_df = 0.5, analyzer = 'word', stop_words = 'english', max_features = 10)

    
corpus_tf_idf = vect.fit_transform(corpus)

print corpus_tf_idf

import re
string = "filename.txt"
str(re.sub('.txt','',string))
