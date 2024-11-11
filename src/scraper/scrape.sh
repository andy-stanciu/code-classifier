#!/bin/bash

# number of problems
COUNT=10

python3 scraper.py \
  -p two-sum -c $COUNT \
  -p add-two-numbers -c $COUNT \
  -p median-of-two-sorted-arrays -c $COUNT \
  -p binary-tree-preorder-traversal -c $COUNT \
  -p binary-tree-postorder-traversal -c $COUNT \
  -p binary-tree-inorder-traversal -c $COUNT \
  -p trapping-rain-water -c $COUNT \
  -p merge-k-sorted-lists -c $COUNT \
  -p target-sum -c $COUNT \
  -p coin-change -c $COUNT