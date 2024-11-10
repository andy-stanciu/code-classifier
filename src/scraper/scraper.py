import os
import re
import requests
import argparse

from scraper_constants import *

def scrape_code(solution_id):
    response = requests.post(LEETCODE_GRAPHQL_URL, 
        json = {
            "query": communitySolution_query, 
            "variables": {
                "topicId": solution_id
            }
        },
        headers = HEADERS)
    
    if response.status_code == 200:
        data = response.json()
        solution_raw = data['data']['topic']['post']['content']
        
        # Use regular expression to find text between triple backticks (code blocks)
        code_blocks = re.findall(r'```(.*?)```', solution_raw, re.DOTALL)

        solutions = []
        for code_block in code_blocks:
            # hacky way to filter out (almost) all non-java solutions/extraneous code blocks
            if code_block.lower().startswith("java []"):
                code_block = code_block.replace("\\n", "\n")  # fix newlines
                code_block = code_block.replace("\\t", "\t")  # fix tabs
                code_block = code_block.replace("\'", "'")  # fix apostrophes
                code_block = re.sub(r'^.*\n', '', code_block, count=1)  # remove first line, which is always "java []"
                code_block = re.sub(r'\s*import\s+[^;]*?;\s*(\r\n|\r|\n)?', '', code_block, flags=re.IGNORECASE)  # remove any import statements
                solutions.append(code_block)
        
        return solutions
    else:
        print(f"Request failed with status code {response.status_code}: {response.text}")

def scrape_solution_ids(question_name, count):
    response = requests.post(LEETCODE_GRAPHQL_URL, 
        json = {
            "query": communitySolutions_query, 
            "variables": {
                "query": "",
                "languageTags": "java",
                "topicTags": "",
                "questionSlug": question_name,
                "skip": 0,
                "first": count,
                "orderBy": "hot"
            }
        },
        headers = HEADERS)

    if response.status_code == 200:
        data = response.json()
        solutions = data['data']['questionSolutions']['solutions']

        solution_ids = []
        for solution in solutions:
            solution_ids.append(solution['id'])
        
        return solution_ids
    else:
        print(f"Request failed with status code {response.status_code}: {response.text}")

def scrape_solutions(question_name, count):
    print(f'Scraping {count} Java LeetCode solutions for {question_name}...')
    solution_ids = scrape_solution_ids(question_name, count)

    i = 1
    solution_dir = os.path.join(SOLUTIONS_DIR, question_name)
    os.makedirs(solution_dir, exist_ok=True)

    for solution_id in solution_ids:
        for solution in scrape_code(solution_id):
            path = os.path.join(solution_dir, f'{question_name}-{i}.txt')
            with open(path, "w") as file:
                file.write(solution)
            i += 1
    
    print(f'Finished scraping solutions (requested: {count}, actual: {i - 1}) to {solution_dir}')

def main():
    parser = argparse.ArgumentParser()

    parser.add_argument('--problem', '-p', action='append', required=True, help='Name of the problem')
    parser.add_argument('--count', '-c', action='append', required=True, help='Number of solutions to scrape for the problem')

    args = parser.parse_args()

    if len(args.problem) != len(args.count):
        parser.error('The number of --problem and --count arguments must be the same.')

    problem_counts = list(zip(args.problem, args.count))
    for problem, count in problem_counts:
        scrape_solutions(problem, count)

if __name__ == "__main__":
    main()
