import csv
import random
import math
import operator

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


def euclideanDistance(instance1, instance2, length):
	distance = 0
	for x in range(length):
		if(x==0):
			continue;
		#print instance1[x]
		distance += pow((instance1[x] - instance2[x]), 2)
	return math.sqrt(distance)

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

def getResponse(neighbors):
	count = 0
	result =0
	for x in range(len(neighbors)):
		##print neighbors[x][0]
		if(neighbors[x][0]=="1"):
		##	print "check"
			count+=1
		else:
			count-=1

	if(count>0):
		return 1
	else:
		return 0

def getAccuracy(testSet, predictions):
	correct = 0
	for x in range(len(testSet)):
		if int(testSet[x][0]) == int(predictions[x]):
			correct += 1
	
	return (correct/float(len(testSet))) * 100.0
	
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
	# generate predictions
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
