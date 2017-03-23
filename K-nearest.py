import csv
import random
import math
import operator

# ---------------------------------------------------------------------------------------------------------------------------------
# Method creates the training set.
# Input
#   ** filename - the name of the data that contains the dataset
#	** trainingSet - the list into which the data is to be loaded
# Returns
#   ** NONE
# ---------------------------------------------------------------------------------------------------------------------------------
def loadDataset(filename, trainingSet=[]):
	with open(filename, 'rb') as csvfile:
	    lines = csv.reader(csvfile)
	    dataset = list(lines)
	    for x in range(len(dataset)-1):
		if(x==0):
			continue;
	        for y in range(9):
		    if(y==0):
			continue;
	            dataset[x][y] = float(dataset[x][y])
	        trainingSet.append(dataset[x])

# ---------------------------------------------------------------------------------------------------------------------------------
# Method calculates the euclidean distance between two data points by taking into consideration all their attributes.
# Input
#   ** instance1 - the name of the data that contains the dataset
#	** instance2 - the list into which the data is to be loaded
#	** length - number of attributes in a particular data point
# Returns
#   ** distance - the euclidean distance between the two provided data points
# ---------------------------------------------------------------------------------------------------------------------------------
def euclideanDistance(instance1, instance2, length):
	distance = 0
	for x in range(length):
		if(x==0):
			continue;
		distance += pow((instance1[x] - instance2[x]), 2)
	return math.sqrt(distance)

# ---------------------------------------------------------------------------------------------------------------------------------
# Computes the neighbours for a particular data point amongst other data points.
# Input
#   ** trainingSet - list of all the training data points
#	** testInstance - data point from the test data whose neighbours are to be determined
#	** k - the number of neighbours to be considered
# Returns
#   ** distance - the euclidean distance between the two provided data points
# ---------------------------------------------------------------------------------------------------------------------------------
def getNeighbors(trainingSet, testInstance, k):
	distances = []
	length = len(testInstance)-1
	for x in range(len(trainingSet)):
		dist = euclideanDistance(testInstance, trainingSet[x], length)
		distances.append((trainingSet[x], dist))
	distances.sort(key=operator.itemgetter(1))
	neighbors = []
	for x in range(k):
		neighbors.append(distances[x][0])
	return neighbors

# ---------------------------------------------------------------------------------------------------------------------------------
# Method is used to predict the outcome i.e. whether the result was Democrat or not based on the result of its closest neighbours.
# Input
#   ** neighbors - list of neighbours of a particular datapoint
# Returns
#   ** 1 or 0 - 1 representing a predicted value of Democrat result, and 0 not Democrat result
# ---------------------------------------------------------------------------------------------------------------------------------
def getResponse(neighbors):
	count = 0
	result =0
	for x in range(len(neighbors)):
		if(neighbors[x][0]=="1"):
			count+=1
		else:
			count-=1

	if(count>0):
		return 1
	else:
		return 0

# ---------------------------------------------------------------------------------------------------------------------------------
# Method to calculate the verify the predictions made and calculate its accuracy.
# Input
#   ** testSet - the test set data containing the actual results.
#	** predictions - the list containing the predicted values for the test set datapoints
# Returns
#   ** accuracy - the accuracy after comparing the actual vs the predicted.
# ---------------------------------------------------------------------------------------------------------------------------------
def getAccuracy(testSet, predictions):
	correct = 0
	for x in range(len(testSet)):
		if int(testSet[x][0]) == int(predictions[x]):
			correct += 1
	
	return (correct/float(len(testSet))) * 100.0

# ---------------------------------------------------------------------------------------------------------------------------------
# Main method.
# ---------------------------------------------------------------------------------------------------------------------------------
def main():
	# prepare data
	trainingSet=[]
	testSet=[]
	split = 0.67
	actual=0;
	predicted=0;
	loadDataset('votes-train.csv', trainingSet)
	loadDataset('votes-test.csv', testSet)
	print 'Train set: ' + repr(len(trainingSet))
	print 'Test set: ' + repr(len(testSet))
	predictions=[]
	k = 3
	for x in range(len(testSet)):
		neighbors = getNeighbors(trainingSet, testSet[x], k)
		result = getResponse(neighbors)
		predictions.append(result)
		print('> predicted=' + repr(result) + ', actual=' + repr(testSet[x][0]))
	accuracy = getAccuracy(testSet, predictions)
	print('Accuracy: ' + repr(accuracy) + '%')
	
main()
