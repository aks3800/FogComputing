from hierarchical_without_neighbourhood import app as hierarchical_without_neighbourhood
from hierarchical_without_neighbourhood.app import (
    HierarchicalWithoutNeighbourhoodArchitecture,
)
from hierarchical_with_neighbourhood import app as hierarchical_with_neighbourhood
from hybrid import app as hybrid

if __name__ == "__main__":
    total_number_of_nodes = 7
    maximum_number_of_children_per_node = 2
    arch = HierarchicalWithoutNeighbourhoodArchitecture(
        maximum_number_of_children_per_node=maximum_number_of_children_per_node
    )
    arch.set_up(total_number_of_nodes=total_number_of_nodes)

    # print(
    #     hierarchical_with_neighbourhood.set_up_hierarchical_architecture_with_neighbourhood(
    #         total_number_of_nodes, maximum_number_of_children_per_node
    #     )
    # )
    # print(
    #     hybrid.set_up_hybrid_architecture(
    #         total_number_of_nodes, maximum_number_of_children_per_node
    #     )
    # )
