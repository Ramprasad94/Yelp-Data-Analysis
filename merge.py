import re
import pandas as pd
f = open("data/ReviewData100k.csv", "r")
reviews = [i.strip().split(",") for i in f]
f.close()

f= open("business_cat.csv", "r")
cats = [i.strip().split(",") for i in f]
cat_dict={}
for i in cats[1:]:
	cat_dict[i[1]]=[]
	for j in i[2:]:
		if j!='':
			cat_dict[i[1]].append(j)
f.close()


for i in range(1,len(reviews)):
	if len(reviews[i])>=4:
		bid = reviews[i][-4]
	if bid in cat_dict:
		cat = cat_dict[bid]
		reviews[i].append(cat)
if type(reviews[-3][-1])==type([]):
	print reviews[-2]

cat_dict={}
for item in reviews:
	text = item[map(len,item).index(max(map(len, item)))]
	if type(item[-1])==type([]):
		for catgiri in item[-1] :
			#print catgiri
			if catgiri in cat_dict:
				if type(text) == type(""):
					cat_dict[catgiri]+=text+" "
			else:
				if type(text) == type(""):
					cat_dict[catgiri]=text+" "

for k,v in cat_dict.items():
	f= open("./cats/"+re.sub("[^a-zA-Z]", "", k)+".txt", "w")
	f.write(v)
	f.close()

