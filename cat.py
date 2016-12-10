import pandas as pd
from collections import defaultdict
#cat= pd.read_csv("categories.csv")


f = open("categories.csv", "r")
cat = [i.strip().split(",") for i in f.readlines()][1:]
f.close()
b_dict= {}

f = open("business_cat.csv", "r")
bcat = [i.strip().split(",") for i in f.readlines()][1:]
f.close()

for rec in bcat:
	#print rec
	bid = rec[1]
	for c in rec[2:]:
		if c != '':
			#print c
			if c in b_dict:
				b_dict[c].append(bid)
			else:
				b_dict[c]= [bid]

f = open("cat_b.csv","w")
for k, v in b_dict.items():
	f.write(str(k)+",")
	for i in v:
		f.write(str(i)+",")
	f.write("\n")
f.close()
