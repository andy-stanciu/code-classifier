import os
import re
from preprocessing_constants import *

# Prunes (deletes) all solutions with solution number greater than MAX_SOLUTION_COUNT

pattern = re.compile(r'\d+')

for category_path in os.listdir(SOLUTIONS_DIR):
    category_folder = os.path.join(SOLUTIONS_DIR, category_path)
    if os.path.isdir(category_folder):
        solutions_folders = [f for f in os.listdir(category_folder) if not f.startswith('.')]
        print(f'Found {len(solutions_folders)} problems in {category_folder}')
        for solution_path in solutions_folders:
            solution_folder = os.path.join(category_folder, solution_path)
            if os.path.isdir(solution_folder):
                solutions = os.listdir(solution_folder)
                print(f'Found {len(solutions)} solutions in {solution_folder}')
                for problem_path in solutions:
                    problem_file = os.path.join(solution_folder, problem_path)
                    if os.path.isfile(problem_file):
                        match = pattern.search(problem_file)
                        if match:
                            number = int(match.group())
                            if number > MAX_SOLUTION_COUNT:
                                os.remove(problem_file)
                                print(f"Deleted: {problem_file}")
