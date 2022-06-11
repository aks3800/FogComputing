import math


def variance(a, n, m):
    """Function for calculating variance a:matrix n: length of row/column m: mean"""
    sum = 0
    for i in range(n):
        for j in range(n):
            # subtracting mean
            # from elements
            a[i][j] -= m

            # a[i][j] = fabs(a[i][j]);
            # squaring each terms
            a[i][j] *= a[i][j]

    # taking sum
    for i in range(n):
        for j in range(n):
            sum += a[i][j]

    return sum / (n * n)
