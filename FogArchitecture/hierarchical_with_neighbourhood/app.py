from hierarchical_with_neighbourhood.node.compute_node import ComputeNode
from hierarchical_with_neighbourhood.node.cloud import Cloud
from hierarchical_with_neighbourhood.node.node import Node


def set_up_hierarchical_architecture_with_neighbourhood(
    total_number_of_nodes: int, maximum_number_of_children_per_node: int
):
    cloud = Cloud(1, maximum_number_of_children_per_node, None)
    print(cloud)
    for i in range(2, total_number_of_nodes):
        create_tree(cloud, i, maximum_number_of_children_per_node)
        print("\n\n")
        print_details(cloud)
    print("\n\n")
    print(shortest_path(cloud, 1, 2))


def get_node_with_id(node: Node, id: int):
    if node.id == id:
        return node
    else:
        if len(node.children) == 0:
            return None
        else:
            for child in node.children:
                n = get_node_with_id(child, id)
                if n:
                    return n


def shortest_path(cloud, source_id, destination_id):
    visited = []
    source_node = get_node_with_id(cloud, source_id)
    queue = [{"node": source_node, "dist": 0}]
    visited.append(source_id)

    while len(queue) > 0:
        element = queue.pop(0)
        n = element.get("node")
        dist = element.get("dist")
        if n.id == destination_id:
            return {"shortestDistance": dist}
        for neighbour in n.neighbourhood:
            if neighbour.id not in visited:
                queue.append({"node": neighbour, "dist": dist + 1})
                visited.append(neighbour.id)

    return {"shortestDistance": -1}


def get_node_with_minimum_children(node) -> ComputeNode:
    minimum_child_node = node.children[0]
    for child_node in node.children:
        if child_node.number_of_nodes < minimum_child_node.number_of_nodes:
            minimum_child_node = child_node
    return minimum_child_node


def update_neighbourhood(parent: Node, neighbour_child: Node):
    parent.add_to_neighbourhood(neighbour_child)
    neighbour_child.add_to_neighbourhood(parent)
    for child_of_parent in parent.children:
        child_of_parent.add_to_neighbourhood(neighbour_child)
        neighbour_child.add_to_neighbourhood(child_of_parent)


def create_tree(node, id, maximum_number_of_children_per_node):
    if len(node.children) < maximum_number_of_children_per_node:
        compute_node = ComputeNode(id, maximum_number_of_children_per_node, node.id)
        node.add_child(compute_node)
        node.child_added()
        update_neighbourhood(node, compute_node)
    else:
        node_with_minimum_children = get_node_with_minimum_children(node)
        node.child_added()
        create_tree(node_with_minimum_children, id, maximum_number_of_children_per_node)


def print_details(node):
    print(node)
    for child in node.children:
        print_details(child)
