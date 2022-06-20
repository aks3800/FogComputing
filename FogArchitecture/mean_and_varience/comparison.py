import math
from mean_and_varience.mean import mean
from mean_and_varience.variance import variance

hierarchical_without_neighbourhood = [
    [0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2],
    [1, 0, 2, 2, 1, 3, 3, 1, 3, 3, 1, 3, 3],
    [1, 2, 0, 2, 3, 1, 3, 3, 1, 3, 3, 1, 3],
    [1, 2, 2, 0, 3, 3, 1, 3, 3, 1, 3, 3, 1],
    [2, 1, 3, 3, 0, 4, 4, 2, 4, 4, 2, 4, 4],
    [2, 3, 1, 3, 4, 0, 4, 4, 2, 4, 4, 2, 4],
    [2, 3, 3, 1, 4, 4, 0, 4, 4, 2, 4, 4, 2],
    [2, 1, 3, 3, 2, 4, 4, 0, 4, 4, 2, 4, 4],
    [2, 3, 1, 3, 4, 2, 4, 4, 0, 4, 4, 2, 4],
    [2, 3, 3, 1, 4, 4, 2, 4, 4, 0, 4, 4, 2],
    [2, 1, 3, 3, 2, 4, 4, 2, 4, 4, 0, 4, 4],
    [2, 3, 1, 3, 4, 2, 4, 4, 2, 4, 4, 0, 4],
    [2, 3, 3, 1, 4, 4, 2, 4, 4, 2, 4, 4, 0],
]

hierarchical_with_neighbourhood = [
    [0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2],
    [1, 0, 1, 1, 1, 2, 2, 1, 2, 2, 1, 2, 2],
    [1, 1, 0, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2],
    [1, 1, 1, 0, 2, 2, 1, 2, 2, 1, 2, 2, 1],
    [2, 1, 2, 2, 0, 3, 3, 1, 3, 3, 1, 3, 1],
    [2, 2, 1, 2, 3, 0, 3, 3, 1, 3, 3, 1, 3],
    [2, 2, 2, 1, 3, 3, 0, 3, 3, 1, 3, 3, 1],
    [2, 1, 2, 2, 1, 3, 3, 0, 3, 3, 3, 1, 3],
    [2, 2, 1, 2, 3, 1, 3, 3, 0, 3, 3, 1, 3],
    [2, 2, 2, 1, 2, 3, 1, 3, 3, 0, 3, 3, 1],
    [2, 1, 2, 2, 1, 3, 3, 1, 3, 3, 0, 3, 3],
    [2, 2, 1, 2, 3, 1, 3, 3, 1, 3, 3, 0, 3],
    [2, 2, 2, 1, 3, 3, 1, 3, 3, 1, 3, 3, 0],
]

hybrid = [
    [0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2],
    [1, 0, 1, 1, 1, 2, 2, 1, 2, 2, 1, 2, 2],
    [1, 1, 0, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2],
    [1, 1, 1, 0, 2, 2, 1, 2, 2, 1, 2, 2, 1],
    [2, 1, 2, 2, 0, 1, 1, 1, 2, 2, 1, 2, 2],
    [2, 2, 1, 2, 1, 0, 1, 2, 1, 2, 2, 1, 2],
    [2, 2, 2, 1, 1, 1, 0, 2, 2, 1, 2, 2, 1],
    [2, 1, 2, 2, 1, 2, 2, 0, 3, 3, 1, 3, 3],
    [2, 2, 1, 2, 2, 1, 2, 2, 0, 3, 3, 1, 3],
    [2, 2, 2, 1, 2, 2, 1, 3, 3, 0, 3, 3, 1],
    [2, 1, 2, 2, 1, 2, 2, 1, 3, 3, 0, 3, 3],
    [2, 2, 1, 2, 2, 1, 2, 3, 1, 3, 3, 0, 3],
    [2, 2, 2, 1, 2, 2, 1, 3, 3, 1, 3, 3, 0],
]

# for mean
m = mean(hierarchical_without_neighbourhood, 13)

# for variance
var = variance(hierarchical_without_neighbourhood, 13, m)

# for standard deviation
dev = math.sqrt(var)

print("hierarchical_without_neighbourhood")

print("Mean:", m)
print("Variance:", var)
print("Deviation:", math.floor(dev))

print("\n\nhierarchical_without_neighbourhood")

# for mean
m = mean(hierarchical_with_neighbourhood, 13)

# for variance
var = variance(hierarchical_with_neighbourhood, 13, m)

# for standard deviation
dev = math.sqrt(var)

print("Mean:", m)
print("Variance:", var)
print("Deviation:", math.floor(dev))

print("\n\nhybrid")

# for mean
m = mean(hybrid, 13)

# for variance
var = variance(hybrid, 13, m)

# for standard deviation
dev = math.sqrt(var)

print("Mean:", m)
print("Variance:", var)
print("Deviation:", math.floor(dev))
