class Node:
    """Base class to represent a basic node"""

    neighbourhood = None

    def __int__(self, node_id, max_children, parent):
        self.max_children = max_children
        self.id = node_id
        self.parent = parent
        self.children = []
        self.neighbourhood = []
        self.number_of_nodes = 1

    def __str__(self):
        return (
            f"node (id={self.id}) (parent={self.parent}) (m={self.max_children}) (n={len(self.children)})"
            f" (total nodes={self.number_of_nodes}) (total neighbours ={len(self.neighbourhood)}"
            f" ({list(map(lambda x: x.id, self.neighbourhood))}))"
        )

    def add_child(self, child):
        """function to add a child node to the list of children"""
        self.children.append(child)

    def child_added(self):
        self.number_of_nodes += 1

    def add_to_neighbourhood(self, node):
        node_already_present = False
        for neighbour in self.neighbourhood:
            if neighbour.id == node.id:
                node_already_present = True
                break

        if not node_already_present and node.id != self.id:
            self.neighbourhood.append(node)
