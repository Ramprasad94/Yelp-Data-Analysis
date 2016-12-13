from __future__ import division, unicode_literals
import math
from textblob import TextBlob as tb
import re
import csv

from sklearn.feature_extraction import text

def tf(word, blob):
    return blob.words.count(word)/ len(blob.words)
    
def n_containing(word, bloblist):
    return sum(1 for blob in bloblist if word in blob.words)

def idf(word, bloblist):
    return math.log(len(bloblist) / (1 + n_containing(word, bloblist)))

def tfidf(word, blob, bloblist):
    return tf(word, blob) * idf(word, bloblist)
    
stop_words = text.ENGLISH_STOP_WORDS

def remove_punctuations(word):
    return re.sub("^a-zA-Z","",word)
    
def remove_stop_words(sentence):
    text_list = re.split(" |\.",str(sentence))
    return ' '.join([word.lower().strip() for word in text_list if remove_punctuations(word.lower()) not in set(stop_words)])
    
import os

path = "E:/Search Final Project/Search-Project/cats/cats"

dirs = os.listdir( path )

corpus = []

for file in dirs:
    corpus.append(file)
#print corpus
    
files_text= {}    


for c in corpus:
    temp_file = open(path+'/'+c, 'r')
    temp_file_text = temp_file.read()
    temp_file_text = remove_stop_words(temp_file_text)    
    files_text[c] = tb(temp_file_text)
    temp_file.close()

bloblist = files_text.values()

final_output = {}
    
for k,v in files_text.items():
    k = str(re.sub('.txt','',k))
    print "Top words in document: "+k+"\n"
    temp_list = []
    scores = {word: tfidf(word, v, bloblist) for word in v.words}    
    sorted_words = sorted(scores.items(), key=lambda x:x[1], reverse=True)
    for word, score in sorted_words[:10]:
        print "Word:"+word+"\t\t"
        print "TF-IDF: ",round(score,5)
        temp_list.append(str(word))
    final_output[k] = temp_list
    
    
csv_file = "E:/Search Final Project/Search-Project/cat_output.csv"
with open(csv_file, 'wb') as csvfile:
    writer = csv.writer(csvfile)
    for key,value in final_output.items():
        writer.writerow([key,value])


  