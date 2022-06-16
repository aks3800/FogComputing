from hybrid.node.compute_node import ComputeNode
from hybrid.node.cloud import Cloud
from hybrid.node.node import Node


class HybridArchitecture:
    def __init__(self, maximum_number_of_children_per_node):
        self.number_of_children = maximum_number_of_children_per_node

    def set_up(self, total_number_of_nodes):
        cloud = Cloud(1, self.number_of_children, None)
        for i in range(2, total_number_of_nodes):
            self.__create_tree(cloud, cloud, i, 0)
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

    def __get_node_with_minimum_children(self, node) -> ComputeNode:
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

    def __get_nodes_on_level(self, root: Node, level: int):
        list_of_nodes = []

        def get_nodes(node: Node, current_level: int, required_level: int):
            if node is None:
                return
            if current_level is required_level:
                list_of_nodes.append(node)
            for children in node.children:
                get_nodes(children, current_level + 1, required_level)

        get_nodes(root, 0, level)
        return list_of_nodes

    def __update_neighbourhood_at_level(self, root, level, node: Node):
        nodes_on_level = self.__get_nodes_on_level(root, level)
        for level_node in nodes_on_level:
            if level_node.id is not node.id:
                level_node.add_to_neighbourhood(node)
                node.add_to_neighbourhood(level_node)

    def __create_tree(self, root, node, id, level):
        if len(node.children) < self.number_of_children:
            compute_node = ComputeNode(id, self.number_of_children, node.id)
            node.add_child(compute_node)
            node.child_added()
            self.__update_neighbourhood(node, compute_node)
            if len(node.children) == 1:
                self.__update_neighbourhood_at_level(root, level + 1, compute_node)
        else:
            node_with_minimum_children = self.__get_node_with_minimum_children(node)
            node.child_added()
            self.__create_tree(root, node_with_minimum_children, id, level + 1)

    def print_details(self, node: Node):
        print(node)
        for child in node.children:
            self.print_details(child)
