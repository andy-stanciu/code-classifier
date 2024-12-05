import sys
import torch
import numpy as np
import warnings
warnings.filterwarnings("ignore")
from graph_util import *
from constants import *
from torch_geometric.utils import from_networkx
from torch_geometric.nn import GCNConv
from torch_geometric.loader import DataLoader
from torch_geometric.data import Dataset
from torch_geometric.nn import global_mean_pool
from torch.nn import Linear
import torch.nn.functional as F

def main():
    if len(sys.argv) < 2:
        print("Fatal: exiting")
        sys.exit(1)

    edges = sys.argv[1]
    graph = read_nodes_from_string(edges)
    data = from_networkx(graph)

    data.x = torch.tensor([node[1]['cooccurrences'] for node in graph.nodes(data=True)], dtype=torch.float)

    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    model = torch.load("../../src/model/GCNModel_7_standard.pth", map_location=torch.device(device))
    model.eval()

    loader = DataLoader([data], batch_size=1, shuffle=False)

    out = None
    with torch.no_grad():
        for batch in loader:
            batch = batch.to(device)
            out = model(batch.x, batch.edge_index, batch.batch)
            _, topk_indices = torch.topk(out, 3, dim=1)  # Get top k class indices

    topk_indices = topk_indices.numpy().flatten()
    out = out.numpy().flatten()
    scores = []
    for k in topk_indices:
        scores.append(out[k])

    scores = softmax(scores)

    i = 1
    for prediction in topk_indices:
        print(f'{i}. {FILES[int(prediction)]} ({scores[i - 1] * 100:.2f}%)$')
        i +=1

class GCN(torch.nn.Module):
    def __init__(self, hidden_channels):
        super(GCN, self).__init__()
        self.conv1 = GCNConv(139, hidden_channels)
        self.conv2 = GCNConv(hidden_channels, hidden_channels)
        self.conv3 = GCNConv(hidden_channels, hidden_channels)
        self.lin = Linear(hidden_channels, 100)

    def forward(self, x, edge_index, batch):
        x = self.conv1(x, edge_index)
        x = x.relu()
        x = self.conv2(x, edge_index)
        x = x.relu()
        x = self.conv3(x, edge_index)
        x = x.relu()

        x = global_mean_pool(x, batch)  
        
        x = F.dropout(x, p=0.5, training=self.training)
        x = self.lin(x)
        return x
    
def softmax(x):
    """Compute softmax values for each element in the list x."""
    e_x = np.exp(x - np.max(x))  # Subtract max(x) for numerical stability
    return e_x / e_x.sum()

if __name__ == "__main__":
    main()