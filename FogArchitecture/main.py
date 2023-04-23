from hierarchical_without_neighbourhood.app import (
    HierarchicalWithoutNeighbourhoodArchitecture,
)
from hierarchical_with_neighbourhood.app import (
    HierarchicalArchitectureWithNeighbourhood,
)

from hybrid.app import (
    HybridArchitecture,
)
from pandas import *
import math
from mean_and_varience.mean import mean
from mean_and_varience.variance import variance


def create_matrix(architecture, cloud, total_nodes):
    rows, cols = (total_nodes, total_nodes)
    arr = []
    for i in range(rows):
        col = []
        for j in range(cols):
            shortestDistance = architecture.shortest_path(cloud, i + 1, j + 1).get(
                "shortestDistance"
            )
            col.append(shortestDistance)
        arr.append(col)
    return arr


if __name__ == "__main__":
    total_number_of_nodes = 19
    maximum_number_of_children_per_node = 3
    # arch = HierarchicalWithoutNeighbourhoodArchitecture(
    #     maximum_number_of_children_per_node=maximum_number_of_children_per_node
    # )
    # cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    # matrix = create_matrix(arch, cloud_node, total_number_of_nodes)
    # print(DataFrame(matrix))
    #
    # # for mean
    # m = mean(matrix, total_number_of_nodes)
    #
    # # for variance
    # var = variance(matrix, total_number_of_nodes, m)
    #
    # # for standard deviation
    # dev = math.sqrt(var)
    #
    # print("hierarchical_without_neighbourhood")
    #
    # print("Mean:", m)
    # print("Variance:", var)
    # print("Deviation:", math.floor(dev))
    #
    # print("\n\n")

    arch = HierarchicalArchitectureWithNeighbourhood(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    matrix = create_matrix(arch, cloud_node, total_number_of_nodes)
    print(DataFrame(matrix))

    # for mean
    m = mean(matrix, total_number_of_nodes)

    # for variance
    var = variance(matrix, total_number_of_nodes, m)

    # for standard deviation
    dev = math.sqrt(var)

    print("hierarchical_with_neighbourhood")

    print("Mean:", m)
    print("Variance:", var)
    print("Deviation:", math.floor(dev))

    print("\n\n")

    arch = HybridArchitecture(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    matrix = create_matrix(arch, cloud_node, total_number_of_nodes)
    print(DataFrame(matrix))

    # for mean
    m = mean(matrix, total_number_of_nodes)

    # for variance
    var = variance(matrix, total_number_of_nodes, m)

    # for standard deviation
    dev = math.sqrt(var)

    print("hybrid")

    print("Mean:", m)
    print("Variance:", var)
    print("Deviation:", math.floor(dev))
