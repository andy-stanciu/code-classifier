To use your own dataset with PyTorch Geometric (PyG), you need to prepare your data in a format that is compatible with PyG's `Data` object. PyTorch Geometric expects a graph dataset to be represented as a list of `Data` objects where each `Data` object contains the graph information (e.g., node features, edge indices, edge attributes, labels).

Below are the steps you need to follow to use your own dataset:

### 1. Prepare Your Graph Data
You need to prepare the graph data in a format that can be used by PyTorch Geometric. Specifically, for each graph, you'll need:

- **`edge_index`**: A tensor of shape `[2, num_edges]`, representing the indices of the edges in the graph. Each edge is defined by a pair of nodes.
- **`x`**: A tensor of shape `[num_nodes, num_node_features]` representing the node features. If you don't have features, this can be `None` or a tensor of zeros.
- **`y`**: A tensor of shape `[num_graphs, num_classes]` representing the graph-level labels (classification). If it's a node classification problem, this could be a tensor of shape `[num_nodes]`.

You can have additional attributes like `edge_attr` (edge features), `pos` (node positions for visualization), etc., depending on your use case.

### 2. Create a `Data` Object
Each individual graph should be converted into a PyTorch Geometric `Data` object. Here's an example:

```python
import torch
from torch_geometric.data import Data

# Example: Let's create a toy graph.
# Suppose we have 3 nodes with 2 features each.
node_features = torch.tensor([[1, 2], [3, 4], [5, 6]], dtype=torch.float)

# Edges between nodes (e.g., node 0 -> node 1, node 1 -> node 2, etc.)
edge_index = torch.tensor([[0, 1, 1, 2], [1, 0, 2, 1]], dtype=torch.long)

# Node labels (e.g., 0 for the first node, 1 for the second, etc.)
labels = torch.tensor([0, 1, 0], dtype=torch.long)

# Create a Data object
data = Data(x=node_features, edge_index=edge_index, y=labels)

# Print the Data object
print(data)
```

This would output a representation of the graph, including node features (`x`), edges (`edge_index`), and labels (`y`).

### 3. Use a Custom Dataset
To use your own dataset, you need to create a custom `Dataset` class that inherits from `torch_geometric.data.InMemoryDataset`. In this class, you'll define how to load your graphs and convert them into `Data` objects.

Here's an example:

```python
import os
import torch
from torch_geometric.data import Dataset, Data
from torch_geometric.utils import from_networkx
import networkx as nx  # NetworkX for graph manipulation

class MyCustomDataset(Dataset):
    def __init__(self, root, transform=None, pre_transform=None):
        super(MyCustomDataset, self).__init__(root, transform, pre_transform)

    def len(self):
        # Return the number of graphs in your dataset
        return len(os.listdir(self.raw_dir))  # Example assuming each graph is a separate file.

    def get(self, idx):
        # Load a graph from your dataset (replace this with your actual loading logic)
        graph_file = os.path.join(self.raw_dir, f"graph_{idx}.txt")  # Example: assuming graph is in a file
        G = nx.read_edgelist(graph_file, nodetype=int)

        # If you have node features, add them as attributes to the nodes
        for node in G.nodes():
            G.nodes[node]['feature'] = [1.0, 2.0]  # Example node feature

        # Convert NetworkX graph to PyG Data object
        data = from_networkx(G)
        
        # Optionally add node features and labels
        data.x = torch.tensor([node['feature'] for node in G.nodes(data=True)], dtype=torch.float)
        data.y = torch.tensor([0, 1, 0], dtype=torch.long)  # Example labels, change as needed
        
        return data

# Initialize your dataset
dataset = MyCustomDataset(root='data/MyCustomDataset')

# Print the first graph's data
print(dataset[0])
```

### 4. Dataset Directory Structure
For your custom dataset, the data should be organized in a structure that is easy to load. For instance, you could store each graph in a separate file and include features and labels within these files.

A possible directory structure might look like this:

```
MyCustomDataset/
    raw/
        graph_0.txt
        graph_1.txt
        graph_2.txt
    processed/
```

### 5. Loading Data
Once you've defined your custom dataset, you can load it just like any other PyG dataset:

```python
dataset = MyCustomDataset(root='data/MyCustomDataset')
print(f'Number of graphs: {len(dataset)}')

# Access a specific graph
data = dataset[0]
print(data)
```

### 6. Training Your Model
After you load your dataset, you can split it into training and test sets (if necessary) and use it to train a graph neural network (GNN).

### Summary
1. Prepare your graph data in a format that can be used with PyTorch Geometric's `Data` objects (i.e., `edge_index`, `x`, `y`, etc.).
2. Optionally, create a custom dataset class by subclassing `InMemoryDataset` to load your data.
3. Use `torch_geometric.utils.from_networkx()` to convert NetworkX graphs into PyG `Data` objects if you're using NetworkX.
4. After that, you can work with your dataset as you would with any other dataset in PyTorch Geometric, including training graph-based neural networks.