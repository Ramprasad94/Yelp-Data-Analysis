# Yelp-Data-Analysis
Repository containing the code for performing data analysis on Yelp reviews and tips.

##Task 1 : Predicting categories of a business from review and tip text

-For each business, create a Lucene document containing the reviews and tip text.
-Proceed to index the Lucene documents.
-Create a query list consisting of all the categories from dataset, where each category will be a query term.
-Compute the scores for each query term. (Score function will be tf-idf)
-Set a threshold value to give out the top 3 category scores as belonging to the business.

### Similarity Used:
- Classic Similarity
- BM25 Similarity
- LMJM Similarity 
- LMD Similarity

### Evaluation measure: 
- Accuracy with penalties for missed and incorrect predictions.


## Task 2: User rating prediction from their review

### Data preprocessing:
- Clean data by removing stop words, punctuations, numbers
- Convert all text data in lower case
- Generate tf-idf matrix from the data
- Normalize

### Data visualization
- Understand the distribution of words
- Feature importance

### Predictive models
- Split train and test data

We have used following machine learning algorithms for this task:
- Linear regression
- Ridge regression
- Lasso regression
- Elasticnet regression
- K-nearest neighbours
- Decision trees
- Extra trees
- Random forest 
- Boosting (Adaboost, gradient boosting)

### Tuning models
- We used 20% of training data as validation set
- Use different objective functions like mean absolute error, mean squared error, root mean squared error etc. to optimize models
 
### Evaluation 
- Root mean squared error
