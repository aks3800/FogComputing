import math

import xlsxwriter

from hierarchical_with_neighbourhood.app import (
    HierarchicalArchitectureWithNeighbourhood,
)
from hierarchical_without_neighbourhood.app import (
    HierarchicalWithoutNeighbourhoodArchitecture,
)
from hybrid.app import (
    HybridArchitecture,
)
from mean_and_varience.mean import mean
from mean_and_varience.variance import variance


def create_matrix(architecture, cloud, total_nodes):
    rows, cols = (total_nodes, total_nodes)
    arr = []
    for i in range(rows):
        col = []
        for j in range(cols):
            shortestDistance = architecture.shortest_path(cloud, i + 1, j + 1).get(
                "shortestDistance"
            )
            col.append(shortestDistance)
        arr.append(col)
    return arr


if __name__ == "__main__":
    total_number_of_nodes = 10
    maximum_number_of_children_per_node = 3
    workbook = xlsxwriter.Workbook("records.xlsx")

    worksheet = workbook.add_worksheet("HierarchicalWithoutNeighbour")

    worksheet.write("A1", "Number of nodes")
    worksheet.write("B1", "Mean")
    worksheet.write("C1", "Variance")
    worksheet.write("D1", "Deviation")
    row = 2

    for x in range(500):
        arch = HierarchicalWithoutNeighbourhoodArchitecture(
            maximum_number_of_children_per_node=maximum_number_of_children_per_node
        )
        cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
        matrix = create_matrix(arch, cloud_node, total_number_of_nodes)

        # for mean
        m = mean(matrix, total_number_of_nodes)

        # for variance
        var = variance(matrix, total_number_of_nodes, m)

        # for standard deviation
        dev = math.sqrt(var)

        worksheet.write(f"A{row}", f"{total_number_of_nodes}")
        worksheet.write(f"B{row}", f"{m}")
        worksheet.write(f"C{row}", f"{var}")
        worksheet.write(f"D{row}", f"{dev}")

        total_number_of_nodes += 1
        row += 1
        print(f"HierarchicalWithoutNeighbour {row}")

    worksheet = workbook.add_worksheet("HierarchicalWithNeighbour")
    worksheet.write("A1", "Number of nodes")
    worksheet.write("B1", "Mean")
    worksheet.write("C1", "Variance")
    worksheet.write("D1", "Deviation")
    row = 2
    total_number_of_nodes = 10

    for x in range(500):
        arch = HierarchicalArchitectureWithNeighbourhood(
            maximum_number_of_children_per_node=maximum_number_of_children_per_node
        )
        cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
        matrix = create_matrix(arch, cloud_node, total_number_of_nodes)

        # for mean
        m = mean(matrix, total_number_of_nodes)

        # for variance
        var = variance(matrix, total_number_of_nodes, m)

        # for standard deviation
        dev = math.sqrt(var)

        worksheet.write(f"A{row}", f"{total_number_of_nodes}")
        worksheet.write(f"B{row}", f"{m}")
        worksheet.write(f"C{row}", f"{var}")
        worksheet.write(f"D{row}", f"{dev}")

        total_number_of_nodes += 1
        row += 1
        print(f"HierarchicalWithNeighbour {row}")

    worksheet = workbook.add_worksheet("HybridArchitecture")
    worksheet.write("A1", "Number of nodes")
    worksheet.write("B1", "Mean")
    worksheet.write("C1", "Variance")
    worksheet.write("D1", "Deviation")
    row = 2
    total_number_of_nodes = 10

    for x in range(500):
        arch = HybridArchitecture(
            maximum_number_of_children_per_node=maximum_number_of_children_per_node
        )
        cloud_node = arch.set_up(total_number_of_nodes=total_number_of_nodes)
        matrix = create_matrix(arch, cloud_node, total_number_of_nodes)

        # for mean
        m = mean(matrix, total_number_of_nodes)

        # for variance
        var = variance(matrix, total_number_of_nodes, m)

        # for standard deviation
        dev = math.sqrt(var)
        worksheet.write(f"A{row}", f"{total_number_of_nodes}")
        worksheet.write(f"B{row}", f"{m}")
        worksheet.write(f"C{row}", f"{var}")
        worksheet.write(f"D{row}", f"{dev}")

        total_number_of_nodes += 1
        row += 1
        print(f"HybridArchitecture {row}")

    workbook.close()
