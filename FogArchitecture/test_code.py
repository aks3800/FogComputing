def sorting_function(e):
    return e[2]


def neighbour_in_queue(list, neighbour_id):
    idx = [i for i, v in enumerate(list) if v[0] == neighbour_id]
    if len(idx) > 0:
        return idx[0]
    return None


def bfs_shortest_path(graph, start, end):
    """
    Implementation of BFS algorithm to find the shortest path between two vertices
    in a weighted graph.
    """
    queue = [(start, [start], 0)]
    visited = set()

    while queue:
        node, path, cost = queue.pop(0)

        if node == end:
            return path, cost

        visited.add(node)

        for neighbor, weight in graph[node].items():

            if neighbor not in visited:
                new_path = path + [neighbor]
                new_cost = cost + weight
                # if neighbour is already present in queue check for the minimum distance.
                neighbour_index_in_queue = neighbour_in_queue(queue, neighbor)
                if neighbour_index_in_queue is None:
                    queue.append((neighbor, new_path, new_cost))
                else:
                    if queue[neighbour_index_in_queue][2] > new_cost:
                        queue[neighbour_index_in_queue] = (neighbor, new_path, new_cost)

        queue.sort(key=sorting_function)

    return None, None


# Example usage:
graph = {
    "A": {"B": 2},
    "B": {"C": 1, "A": 2, "F": 9},
    "C": {"D": 1, "E": 3, "F": 1},
    "D": {"E": 1, "C": 1},
    "E": {"D": 1, "C": 3, "F": 1},
    "F": {"B": 9, "E": 1, "C": 1},
}

start = "A"
end = "F"
shortest_path, cost = bfs_shortest_path(graph, start, end)

print("Shortest path:", shortest_path)
print("Cost:", cost)
