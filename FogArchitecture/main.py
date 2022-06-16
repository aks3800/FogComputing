from hierarchical_without_neighbourhood import app as hierarchical_without_neighbourhood
from hierarchical_with_neighbourhood import app as hierarchical_with_neighbourhood
from hybrid import app as hybrid

if __name__ == "__main__":
    total_number_of_nodes = 18
    maximum_number_of_children_per_node = 3
    print(
        hierarchical_without_neighbourhood.set_up_hierarchical_architecture_without_neighbourhood()
    )
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
