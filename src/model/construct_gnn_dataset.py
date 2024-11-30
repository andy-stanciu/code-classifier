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

TOTAL_SOLUTION_COUNT = 500
PROBLEM_COUNT = 10

FILES = [
'koko-eating-bananas',
'two-sum', 
'binary-tree-preorder-traversal',
'binary-tree-postorder-traversal',
'merge-k-sorted-lists',

'minimum-number-of-arrows-to-burst-balloons',
'kth-smallest-element-in-a-bst',
'intersection-of-two-arrays',
'longest-palindromic-substring',
'combinations',

'3sum',
'4sum', 
'majority-element',
'generate-parentheses',
'valid-anagram',

'product-of-array-except-self',
'median-of-two-sorted-arrays',
'integer-to-roman', 
'single-number',
'longest-consecutive-sequence', 

'sqrtx', 'find-the-index-of-the-first-occurrence-in-a-string',  
 'plus-one', 'linked-list-cycle', 'jump-game',   
'minimum-size-subarray-sum',  'unique-paths-ii', 'permutations', 'coin-change', 
'group-anagrams', 'maximum-subarray', 'counting-bits', 'remove-nth-node-from-end-of-list', 'longest-common-prefix', 
 'remove-duplicates-from-sorted-array-ii', 'is-subsequence', 
'best-time-to-buy-and-sell-stock', 'odd-even-linked-list', 'game-of-life', 'happy-number', 'valid-palindrome', 
'sum-of-left-leaves', 'find-minimum-in-rotated-sorted-array', 'first-missing-positive',  
'binary-tree-zigzag-level-order-traversal', 'average-of-levels-in-binary-tree', 'remove-duplicates-from-sorted-array', 
'combination-sum-ii', 'target-sum',  'merge-sorted-array', 
'evaluate-reverse-polish-notation', 'reverse-nodes-in-k-group',  '3sum-closest', 'swap-nodes-in-pairs', 
'word-pattern', 'palindrome-partitioning', 'reverse-linked-list',  'count-complete-tree-nodes', 
'best-time-to-buy-and-sell-stock-ii', 'pascals-triangle', 'delete-node-in-a-bst', 'move-zeroes', 'missing-number', 
'course-schedule',  'third-maximum-number', 'sort-characters-by-frequency', 
'climbing-stairs', 'linked-list-cycle-ii', 'permutations-ii', 'course-schedule-ii', 
'kth-largest-element-in-an-array', 'symmetric-tree', 'find-pivot-index', 'non-overlapping-intervals', 'rotate-image', 
'path-sum-ii', 'gas-station',  'zigzag-conversion', 'asteroid-collision', 'majority-element-ii', 
'powx-n', 'longest-increasing-subsequence', 'reverse-words-in-a-string', 'minimum-window-substring', 'next-permutation', 
'sort-colors', 'add-two-numbers-ii', 'word-break', 'string-compression', 'merge-two-sorted-lists', 'binary-tree-paths', 
'search-in-rotated-sorted-array', 'palindrome-number', 'path-sum', 'binary-tree-inorder-traversal', 
'binary-tree-maximum-path-sum', 'contains-duplicate-ii', 'ransom-note', 'add-two-numbers', 
'intersection-of-two-arrays-ii', 
]

class SolutionDataset(Dataset):
    def __init__(self, root, transform=None, pre_transform=None):
        super(SolutionDataset, self).__init__()
        self.root = root
        self.transform = transform
        self.pre_transform = pre_transform
        
        # Select random PROBLEM_COUNT solution folders
        self.selected_folders = FILES[:PROBLEM_COUNT]
        
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
