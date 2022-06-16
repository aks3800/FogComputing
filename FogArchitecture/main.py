from hierarchical_without_neighbourhood.app import (
    HierarchicalWithoutNeighbourhoodArchitecture,
)
from hierarchical_with_neighbourhood.app import (
    HierarchicalArchitectureWithNeighbourhood,
)

from hybrid.app import (
    HybridArchitecture,
)

if __name__ == "__main__":
    total_number_of_nodes = 7
    maximum_number_of_children_per_node = 2
    arch = HierarchicalWithoutNeighbourhoodArchitecture(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    print(arch.shortest_path(cloud_node, 4, 5))

    print("\n\n")

    arch = HierarchicalArchitectureWithNeighbourhood(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    print(arch.shortest_path(cloud_node, 4, 5))

    print("\n\n")

    arch = HybridArchitecture(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
    print(arch.shortest_path(cloud_node, 4, 5))
