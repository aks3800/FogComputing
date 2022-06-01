from HierarchicalWithoutNeighbourhood.node.compute_node import ComputeNode
from HierarchicalWithoutNeighbourhood.node.cloud import Cloud

number_of_children = 3
"""number of children a node can have"""


def set_up_hierarchical_architecture_without_neighbourhood():
    cloud = Cloud(1, number_of_children, None)
    print(cloud)
    for i in range(2, 7):
        create_tree(cloud, i)
        print('\n\n')
        print_details(cloud)


def get_node_with_minimum_children(node) -> ComputeNode:
    minimum_child_node = node.children[0]
    for child_node in node.children:
        if child_node.number_of_nodes < minimum_child_node.number_of_nodes:
            minimum_child_node = child_node
    return minimum_child_node


def create_tree(node, id):
    if len(node.children) < number_of_children:
        compute_node = ComputeNode(id, number_of_children, node.id)
        node.add_child(compute_node)
        node.child_added()
    else:
        node_with_minimum_children = get_node_with_minimum_children(node)
        node.child_added()
        create_tree(node_with_minimum_children, id)


def get_height_of_tree(node):
    if len(node.children) == 0:
        return 0
    else:
        max_depth = 0
        for child in node.children:
            max_depth = max(max_depth, get_height_of_tree(child))

        return max_depth + 1


def print_details(node):
    print(node)
    for child in node.children:
        print_details(child)
