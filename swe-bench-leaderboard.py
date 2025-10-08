#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display SWE bench leaderboard data.
"""

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard_data():
    """Fetch leaderboard data from the SWE bench GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board_name, top_n=None):
    """Display the specified leaderboard."""
    # The data structure has a 'leaderboards' key containing an array of leaderboard objects
    leaderboards = data.get('leaderboards', [])
    
    # Find the requested board
    board_data = None
    for board in leaderboards:
        if board.get('name') == board_name:
            board_data = board.get('results', [])
            break
    
    if not board_data:
        available_boards = [board.get('name') for board in leaderboards]
        print(f"Error: Board '{board_name}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    if top_n:
        board_data = board_data[:top_n]
    
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 50)
    
    for i, entry in enumerate(board_data, 1):
        print(f"{i}. {entry.get('name', 'N/A')}")
        print(f"   Resolved: {entry.get('resolved', 'N/A')}%")
        print(f"   Model: {entry.get('tags', ['N/A'])[0] if entry.get('tags') else 'N/A'}")
        print(f"   Date: {entry.get('date', 'N/A')}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
