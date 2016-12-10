import pandas as pd

data = pd.read_csv("data/BusinessData.csv")
cat = data['categories'].tolist()
new_cat=[]
for item in cat:
	new_cat.append(item.replace("[","").replace("]","").split(","))
fin = list(set([item.replace('u\'','').replace('\'','').strip() for sublist in new_cat for item in sublist]))
print len(fin), fin[0:4]
f= open("categories.csv", "w")
for item in fin:
	f.write(item + "\n")
f.close()

#data= pd.concat([data['business_id'],data['categories']], axis=1)
#data.to_csv("business_cat.csv", spe=",")
#print data.head(n=1)
