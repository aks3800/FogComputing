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
    total_number_of_nodes = 7
    maximum_number_of_children_per_node = 3
    arch = HierarchicalWithoutNeighbourhoodArchitecture(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    matrix = create_matrix(arch, cloud_node, total_number_of_nodes)
    print(DataFrame(matrix))

    print("\n\n")

    arch = HierarchicalArchitectureWithNeighbourhood(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    matrix = create_matrix(arch, cloud_node, total_number_of_nodes)
    print(DataFrame(matrix))

    print("\n\n")

    arch = HybridArchitecture(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    matrix = create_matrix(arch, cloud_node, total_number_of_nodes)
    print(DataFrame(matrix))
