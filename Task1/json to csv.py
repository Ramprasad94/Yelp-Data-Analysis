import json
import csv
import sys

# open a file for writing

review_data = open('C:/Users/Sagar/PycharmProjects/Convert json to  csv/ReviewData.csv', 'wb')

# create the csv writer object

csvwriter = csv.writer(review_data)
count = 0

with open('yelp_academic_dataset_tip.json') as f:
# with open('demo.json') as f:
    for line in f:
        # print line
        line = '{"details":[' + line + ']}'
        review_parsed = json.loads(line)
        review = review_parsed['details']

        for r in review:

            if count == 0:
                header = r.keys()

                csvwriter.writerow((header))

                count += 1
            try:
                ascii_list= []
                for x in r.values():
                    if isinstance(x, unicode):
                        ascii_list.append(x.encode('ascii','ignore'))
                    else:
                        ascii_list.append(x)
                csvwriter.writerow(ascii_list)
            except:
                print sys.exc_info()[0]

review_data.close()
