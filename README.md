# Code classifier
This is a UW CSE493g1 deep learning project by students Andy Stanciu & Rich Chen. The goal: given a Leetcode solution, identify the problem it is trying to solve. Exploring various neural network code classification architectures including (but not limited to) AST-based GNNs, CNNs, LSTMs, and potentially transformers.

## Contents
- `src/scraper` - GraphQL based web scraper implemented in Python. Used to retrieve raw, unprocessed community solutions for various Leetcode solutions written in Java.
- `src/parser` - Java parser used to optionally preprocess solution data prior to feeding it into our models. The parser currently has the following capabilities, all of which are optional:
  1. Redacting "sensitive identifiers" from the source code, i.e. if any permutation of the problem title appears within an identifier in the solution, it is replaced with a generic "method1" or "var1" identifier.
  2. Stripping comments from the source code.
  3. Standardizing the formatting and whitespace of the source code.

## Instructions
1. Scrape Leetcode problem solutions. Example:
   ```
   python3 src/scraper/scraper.py --problems two-sum --count 100 
   ```
   If successful, scraped solutions can be found in `solutions/raw/two-sum`.
2. (Optional) Preprocess Leetcode solutions by standardizing their formatting, redacting sensitive identifiers, and/or stripping comments. 
   1. Execute `src/parser/src/main/me/andystanciu/Main.java`.
   2. If successful, for each (compiling) solution `foo` for LeetCode problem `Foo` in `solutions/raw/Foo/foo.txt`, an equivalent redacted and stripped + redacted version can be found in `solutions/redacted/Foo/foo.txt` and `solutions/redacted-stripped/Foo/foo.txt`, respectively.
3. Work in progress...