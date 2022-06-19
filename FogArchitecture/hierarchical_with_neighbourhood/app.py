from hierarchical_with_neighbourhood.node.compute_node import ComputeNode
from hierarchical_with_neighbourhood.node.cloud import Cloud
from hierarchical_with_neighbourhood.node.node import Node


class HierarchicalArchitectureWithNeighbourhood:
    def __init__(self, maximum_number_of_children_per_node):
        self.number_of_children = maximum_number_of_children_per_node

    def set_up(self, total_number_of_nodes):
        cloud = Cloud(1, self.number_of_children, None)
        for i in range(2, total_number_of_nodes + 1):
            self.__create_tree(cloud, i)
        return cloud

    def __get_node_with_id(self, node: Node, id: int):
        if node.id == id:
            return node
        else:
            if len(node.children) == 0:
                return None
            else:
                for child in node.children:
                    n = self.__get_node_with_id(child, id)
                    if n:
                        return n

    def shortest_path(self, cloud, source_id, destination_id):
        visited = []
        source_node = self.__get_node_with_id(cloud, source_id)
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

    def __get_node_with_minimum_children(sefl, node: Node) -> ComputeNode:
        minimum_child_node = node.children[0]
        for child_node in node.children:
            if child_node.number_of_nodes < minimum_child_node.number_of_nodes:
                minimum_child_node = child_node
        return minimum_child_node

    def __update_neighbourhood(self, parent: Node, neighbour_child: Node):
        parent.add_to_neighbourhood(neighbour_child)
        neighbour_child.add_to_neighbourhood(parent)
        for child_of_parent in parent.children:
            child_of_parent.add_to_neighbourhood(neighbour_child)
            neighbour_child.add_to_neighbourhood(child_of_parent)

    def __create_tree(self, node, id):
        if len(node.children) < self.number_of_children:
            compute_node = ComputeNode(id, self.number_of_children, node.id)
            node.add_child(compute_node)
            node.child_added()
            self.__update_neighbourhood(node, compute_node)
        else:
            node_with_minimum_children = self.__get_node_with_minimum_children(node)
            node.child_added()
            self.__create_tree(
                node_with_minimum_children,
                id,
            )

    def print_details(self, node: Node):
        print(node)
        for child in node.children:
            self.print_details(child)
