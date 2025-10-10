#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard.
"""

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard():
    """Fetch the leaderboard data from the SWE bench GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.load(response)
        return data
    except Exception as e:
        print(f"Error fetching leaderboard: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board_name, top_n=None):
    """Display the specified leaderboard."""
    # Handle different possible data structures
    if isinstance(data, dict):
        if board_name in data:
            board_data = data[board_name]
        elif "leaderboards" in data:
            board_data = data["leaderboards"]
        else:
            print(f"Error: Board '{board_name}' not found. Available keys: {', '.join(data.keys())}", file=sys.stderr)
            sys.exit(1)
    else:
        board_data = data
    
    # If board_data is a dict, try to find the specific board
    if isinstance(board_data, dict):
        if board_name in board_data:
            board_data = board_data[board_name]
        else:
            print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(board_data.keys())}", file=sys.stderr)
            sys.exit(1)
    
    if top_n:
        board_data = board_data[:top_n]
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    for i, entry in enumerate(board_data, 1):
        # Handle different possible entry formats
        if isinstance(entry, dict):
            model = entry.get('model', entry.get('name', 'Unknown'))
            score = entry.get('score', entry.get('value', 0))
        else:
            model = str(entry)
            score = "N/A"
        
        print(f"{i:2d}. {model}: {score}")
    
    print()


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard type (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
