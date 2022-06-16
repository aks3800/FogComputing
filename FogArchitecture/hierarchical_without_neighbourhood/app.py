from hierarchical_without_neighbourhood.node.compute_node import ComputeNode
from hierarchical_without_neighbourhood.node.cloud import Cloud
from hierarchical_without_neighbourhood.node.node import Node


class HierarchicalWithoutNeighbourhoodArchitecture:
    def __init__(self, maximum_number_of_children_per_node):
        self.number_of_children = maximum_number_of_children_per_node

    def set_up(self, total_number_of_nodes):
        cloud = Cloud(1, self.number_of_children, None)
        print(cloud)
        for i in range(2, total_number_of_nodes):
            self.__create_tree(cloud, i)
            print("\n\n")
            self.print_details(cloud)

        print("\n\n")
        print(self.shortest_path(cloud, 4, 5))

    def __get_node_with_minimum_children(self, node: Node) -> ComputeNode:
        minimum_child_node = node.children[0]
        for child_node in node.children:
            if child_node.number_of_nodes < minimum_child_node.number_of_nodes:
                minimum_child_node = child_node
        return minimum_child_node

    def __create_tree(self, node, id):
        if len(node.children) < self.number_of_children:
            compute_node = ComputeNode(id, self.number_of_children, node.id)
            node.add_child(compute_node)
            node.child_added()
        else:
            node_with_minimum_children = self.__get_node_with_minimum_children(node)
            node.child_added()
            self.__create_tree(node_with_minimum_children, id)

    def get_height_of_tree(self, node: Node):
        if len(node.children) == 0:
            return 0
        else:
            max_depth = 0
            for child in node.children:
                max_depth = max(max_depth, self.get_height_of_tree(child))

            return max_depth + 1

    def print_details(self, node: Node):
        print(node)
        for child in node.children:
            self.print_details(child)

    def __find_lca(self, root: Node, source_id: int, destination_id: int) -> Node:
        if root is None:
            return root

        if root.id == source_id or root.id == destination_id:
            return root

        list_of_common_ancestors = [None] * self.number_of_children
        for i in range(self.number_of_children):
            if len(root.children) == 0:
                list_of_common_ancestors[i] = self.__find_lca(
                    None, source_id, destination_id
                )
            else:
                child_node = None
                if i < len(root.children):
                    child_node = root.children[i]
                list_of_common_ancestors[i] = self.__find_lca(
                    child_node, source_id, destination_id
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

    def __find_distance_from_ancestor_node(self, root: Node, node_id: int) -> int:
        if root is None:
            return -1
        if root.id == node_id:
            return 0

        distance_list = [None] * self.number_of_children
        for i in range(self.number_of_children):
            if len(root.children) == 0:
                distance_list[i] = self.__find_distance_from_ancestor_node(
                    None, node_id
                )
            else:
                child_node = None
                if i < len(root.children):
                    child_node = root.children[i]
                distance_list[i] = self.__find_distance_from_ancestor_node(
                    child_node, node_id
                )

        distance = max(distance_list)
        return distance + 1 if distance >= 0 else -1

    def __find_distance_between_two_nodes(
        self, root: Node, source_id: int, destination_id: int
    ):
        lca = self.__find_lca(root, source_id, destination_id)

        return (
            self.__find_distance_from_ancestor_node(lca, source_id)
            + self.__find_distance_from_ancestor_node(lca, destination_id)
            if lca
            else -1
        )

    def shortest_path(self, cloud, source_id, destination_id):
        dist = self.__find_distance_between_two_nodes(cloud, source_id, destination_id)
        return {"shortestDistance": dist}


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
