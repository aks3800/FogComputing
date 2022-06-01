class Node:
    """Base class to represent a basic node"""

    def __int__(self, node_id, max_children, parent):
        self.max_children = max_children
        self.id = node_id
        self.parent = parent
        self.children = []
        self.number_of_nodes = 1

    def __str__(self):
        return f'node (id={self.id}) (parent={self.parent}) (m={self.max_children}) (n={len(self.children)})' \
               f' (total nodes={self.number_of_nodes})'

    def add_child(self, child):
        """function to add a child node to the list of children"""
        self.children.append(child)

    def child_added(self):
        self.number_of_nodes += 1
