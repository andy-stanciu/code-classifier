import os
import pickle
import random
from sklearn.feature_extraction.text import CountVectorizer

base_dir = '../../solutions/raw'

data = []
labels = []
categories = []

for category in os.listdir(base_dir):
    category_path = os.path.join(base_dir, category)
    if os.path.isdir(category_path):
        categories.append(category)
        for file_name in os.listdir(category_path):
            file_path = os.path.join(category_path, file_name)
            if os.path.isfile(file_path) and file_name.endswith('.txt'):
                with open(file_path, 'r', encoding='utf-8') as file:
                    content = file.read()
                    data.append(content)
                    labels.append(category)

vectorizer = CountVectorizer()
X = vectorizer.fit_transform(data)

dataset = {
    'data': X,
    'labels': labels,
    'categories': categories,
    'vectorizer': vectorizer
}

output_file = '../../data/model-pickle/solutions_dataset_transformer_sequences.pkl'
with open(output_file, 'wb') as f:
    pickle.dump(dataset, f)

print(f"Dataset successfully saved to {output_file}")
