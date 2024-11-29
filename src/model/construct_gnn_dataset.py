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

TOTAL_SOLUTION_COUNT = 500
PROBLEM_COUNT = 5

class SolutionDataset(Dataset):
    def __init__(self, root, transform=None, pre_transform=None):
        super(SolutionDataset, self).__init__()
        self.root = root
        self.transform = transform
        self.pre_transform = pre_transform
        
        # Select random PROBLEM_COUNT solution folders
        all_folders = [f for f in os.listdir(root) if os.path.isdir(os.path.join(root, f))]
        self.selected_folders = random.sample(all_folders, PROBLEM_COUNT)
        
        # Gather the first TOTAL_SOLUTION_COUNT files in each folder
        self.solution_files = []
        for folder_idx, folder in enumerate(self.selected_folders):
            folder_path = os.path.join(root, folder)
            files = sorted(os.listdir(folder_path))[:TOTAL_SOLUTION_COUNT]
            self.solution_files.extend([(folder_idx, folder, os.path.join(folder_path, f)) for f in files])
    
    def len(self):
        return len(self.solution_files)

    def get(self, idx):
        folder_idx, folder, path = self.solution_files[idx]
        graph = read_nodes(path)
        data = from_networkx(graph)
        
        # Create one-hot label for the folder (problem) index
        label = np.zeros(PROBLEM_COUNT)
        label[folder_idx] = 1
        
        data.x = torch.tensor([node[1]['cooccurrences'] for node in graph.nodes(data=True)], dtype=torch.float)
        data.y = torch.tensor(label, dtype=torch.long)

        return data

    def visualize(self, idx):
        folder_idx, folder, path = self.solution_files[idx]
        graph = read_nodes(path)
        pos = hierarchy_pos(graph)
        fig = plt.figure(figsize=(10, 8))
        fig.canvas.manager.set_window_title(folder)
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
        plt.title(folder)
        plt.show()
    
    def get_solution_path(self, idx):
        return self.solution_files[idx]

raw_solutions = SolutionDataset(root='../../data/raw')
print(f'total solution count: {len(raw_solutions)}')

with open('../../data/model-pickle/solutions_dataset_gnn_graphs.pkl', 'wb') as f:
    pickle.dump(raw_solutions, f)

print("Dataset serialized and saved to '../../data/model-pickle/solutions_dataset_gnn_graphs.pkl'")

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
