from hierarchical_with_neighbourhood.node.node import Node


class ComputeNode(Node):
    def __init__(self, node_id, children, parent):
        Node.__int__(self, node_id, children, parent)

    def __str__(self):
        return f"Compute {super().__str__()}"
