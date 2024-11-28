import os
import torch
import numpy as np
import random
from torch_geometric.data import Dataset, Data
from torch_geometric.utils import from_networkx
from graph_util import *
import networkx as nx
import matplotlib.pyplot as plt
import pickle
from tqdm import tqdm

SOLUTION_COUNT = 500
PROBLEM_COUNT = 100

class SolutionDataset(Dataset):
    def __init__(self, root, transform=None, pre_transform=None):
        super(SolutionDataset, self).__init__(root, transform, pre_transform)

    def len(self):
        solutions = os.listdir(self.root)
        solution_count = 0
        for solution in solutions:
            solution_folder = os.path.join(self.root, solution)
            if os.path.isdir(solution_folder):
                solution_count += len(os.listdir(solution_folder))

        return solution_count

    def get(self, idx):
        _, problem_idx, path = self.get_solution_path(idx)
        graph = read_nodes(path)
        data = from_networkx(graph)

        label = np.zeros(PROBLEM_COUNT)
        label[problem_idx] = 1

        data.x = torch.tensor([node[1]['cooccurrences'] for node in graph.nodes(data=True)], dtype=torch.float)
        data.y = torch.tensor(label, dtype=torch.long)

        return data

    def visualize(self, idx):
        solution, _, path = self.get_solution_path(idx)
        graph = read_nodes(path)
        pos = hierarchy_pos(graph)
        fig = plt.figure(figsize=(10, 8))
        fig.canvas.manager.set_window_title(solution)
        nx.draw(
            graph,
            pos,
            with_labels=True,
            arrows=True,
            node_size=25,
            node_color="lightblue",
            font_size=8,
            font_color="black",
            edge_color="gray"
        )
        plt.title(solution)
        plt.show()

    def get_solution_path(self, idx):
        # map idx to the correct solution file
        solutions = sorted([f for f in os.listdir(self.root) if f != '.DS_Store'])
        solution_idx = idx // SOLUTION_COUNT
        solution_num = (idx % SOLUTION_COUNT) + 1
        solution = solutions[solution_idx]
        solution_formatted = f'{solution}-{solution_num}'
        return solution_formatted, solution_idx, os.path.join(self.root, f'{solution}/{solution_formatted}.edges')

raw_solutions = SolutionDataset(root='../../data/raw')
print(f'total solution count: {len(raw_solutions)}')

pickle_file = '../../data/model-train-pickle/raw_solutions.pkl'
solution_data = []
for i in tqdm(range(len(raw_solutions)), desc="Processing Solutions"):
    try:
        data = raw_solutions.get(i) 
        solution_data.append(data)   
    except Exception as e:
        print(f"Error processing solution at index {i}: {e}")

with open(pickle_file, 'wb') as f:
    pickle.dump(solution_data, f)

print(f'Saved {len(solution_data)} solutions to {pickle_file}')


# sample 1 random solution
solution_nums = random.sample(range(0, len(raw_solutions)), k=1)
for solution_num in solution_nums:
    raw_solutions.visualize(solution_num)
    data = raw_solutions.get(solution_num)
    solution, _, _ = raw_solutions.get_solution_path(solution_num)
    print('==========================')
    print(f'Solution: {solution}')
    print(data)
    print(f'Number of nodes: {data.num_nodes}')
    print(f'Number of edges: {data.num_edges}')
    print(f'Average node degree: {data.num_edges / data.num_nodes:.2f}')
    print(f'Has isolated nodes: {data.has_isolated_nodes()}')
    print(f'Has self-loops: {data.has_self_loops()}')
    print(f'Is undirected: {data.is_undirected()}')
    print(f'x: {data.x}')
    print(f'y: {data.y}')
    print('==========================')