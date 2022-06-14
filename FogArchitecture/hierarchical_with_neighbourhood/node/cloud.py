from hierarchical_with_neighbourhood.node.node import Node


class Cloud(Node):
    def __init__(self, node_id, children, parent):
        Node.__int__(self, node_id, children, parent)

    def __str__(self):
        return f"Cloud {super().__str__()}"
