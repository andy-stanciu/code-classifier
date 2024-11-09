import re
import requests

# Define the GraphQL query
query = """
query communitySolution($topicId: Int!) {
  topic(id: $topicId) {
    post {
      content
    }
  }
}
"""

# Define the endpoint and headers
url = "https://leetcode.com/graphql/"
headers = {
    "Content-Type": "application/json",
    "User-Agent": "Mozilla/5.0",
    "Origin": "https://leetcode.com"
}

# Define the query variables
variables = {
    "topicId": 4828571 # Community solution id to query
}

# Make the request
response = requests.post(url, json={"query": query, "variables": variables}, headers=headers)

# Check the response status and print the result
if response.status_code == 200:
    data = response.json()
    solution_raw = data['data']['topic']['post']['content']
    
    # Use regular expression to find text between triple backticks (code blocks)
    code_blocks = re.findall(r'```(.*?)```', solution_raw, re.DOTALL)

    # Print the extracted content
    for i, code_block in enumerate(code_blocks, 1):
        code_block = code_block.replace("\\n", "\n") # fix newlines
        print(f"Solution #{i}:\n{code_block}\n")
else:
    print(f"Request failed with status code {response.status_code}: {response.text}")
