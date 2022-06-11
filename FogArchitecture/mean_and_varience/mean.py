import math


def mean(a, n):
    """Function for calculating mean"""
    sum = 0
    for i in range(n):
        for j in range(n):
            sum += a[i][j]

    # Returning mean
    return sum / (n * n)
