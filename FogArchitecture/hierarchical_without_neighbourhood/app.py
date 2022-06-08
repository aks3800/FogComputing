from hierarchical_without_neighbourhood.node.compute_node import ComputeNode
from hierarchical_without_neighbourhood.node.cloud import Cloud
from hierarchical_without_neighbourhood.node.node import Node

number_of_children = 2
"""number of children a node can have"""


def set_up_hierarchical_architecture_without_neighbourhood():
    cloud = Cloud(1, number_of_children, None)
    print(cloud)
    for i in range(2, 8):
        create_tree(cloud, i)
        print("\n\n")
        print_details(cloud)

    print("\n\n")
    distance = find_distance_between_two_nodes(cloud, 4, 5)
    print(distance)


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


def find_lca(root: Node, source_id: int, destination_id: int) -> Node:
    if root is None:
        return root

    if root.id == source_id or root.id == destination_id:
        return root

    list_of_common_ancestors = [None] * number_of_children
    for i in range(number_of_children):
        if len(root.children) == 0:
            list_of_common_ancestors[i] = find_lca(None, source_id, destination_id)
        else:
            list_of_common_ancestors[i] = find_lca(
                root.children[i], source_id, destination_id
            )

    if None not in list_of_common_ancestors:
        return root
    else:
        return_item = None
        for item in list_of_common_ancestors:
            if item:
                return_item = item
                break
        return return_item


def find_distance_from_ancestor_node(root: Node, node_id: int) -> int:
    if root is None:
        return -1
    if root.id == node_id:
        return 0

    distance_list = [None] * number_of_children
    for i in range(number_of_children):
        if len(root.children) == 0:
            distance_list[i] = find_distance_from_ancestor_node(None, node_id)
        else:
            distance_list[i] = find_distance_from_ancestor_node(
                root.children[i], node_id
            )

    distance = max(distance_list)
    return distance + 1 if distance >= 0 else -1


def find_distance_between_two_nodes(root: Node, source_id: int, destination_id: int):
    lca = find_lca(root, source_id, destination_id)

    return (
        find_distance_from_ancestor_node(lca, source_id)
        + find_distance_from_ancestor_node(lca, destination_id)
        if lca
        else -1
    )


"""

                            cloud
                            /   \
                           /     \
                          2       3
                         / \     / \
                        /   \   /   \
                       4     6 5     7


find_lca(cloud, 1, 3)
    left = find_lca(1, 1, 3)
        return 1
    
    right = find_lca(2, 1, 3)
        return None

    return 1



find_lca(cloud, 3, 4)
    left = find_lca(1, 3, 4)
        left = find_lca(3, 3, 4)
            return 3
        
        
        right = find_lca(4, 3, 4)
            return 4
        
        return 1
    
    
    right = find_lca(2, 3, 4)
        left = find_lca(5, 3, 4)
            left = find_lca(None, 3, 4)
                return None
                
            right = find_lca(None, 3, 4)
                return None
            
            return None
        
        right = find_lca(6, 3, 4)
            return None


"""
