#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard data.
"""

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard_data():
    """Fetch the leaderboard data from the GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board_name, top=None):
    """Display the specified leaderboard."""
    leaderboards = data.get('leaderboards', [])
    
    # Find the requested board
    board = None
    for lb in leaderboards:
        if lb.get('name') == board_name:
            board = lb
            break
    
    if board is None:
        available_boards = [lb.get('name') for lb in leaderboards]
        print(f"Error: Board '{board_name}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    results = board.get('results', [])
    
    if top is not None and top > 0:
        results = results[:top]
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    for i, result in enumerate(results, 1):
        name = result.get('name', 'N/A')
        resolved = result.get('resolved', 'N/A')
        print(f"{i}. {name}: {resolved}%")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display")
    parser.add_argument("--top", type=int, help="Show only top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
