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


def get_board_data(data, board_name):
    """Get data for a specific board name."""
    for board in data.get('leaderboards', []):
        if board.get('name') == board_name:
            return board
    return None


def display_leaderboard(board_data, top_n=None):
    """Display the specified leaderboard."""
    if not board_data:
        print(f"Error: Board not found.", file=sys.stderr)
        sys.exit(1)
    
    board_name = board_data.get('name', 'Unknown')
    results = board_data.get('results', [])
    
    # Sort by resolved percentage (descending)
    results.sort(key=lambda x: x.get('resolved', 0), reverse=True)
    
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 60)
    
    if top_n is not None:
        results = results[:top_n]
    
    for i, result in enumerate(results, 1):
        name = result.get('name', 'Unknown')
        resolved = result.get('resolved', 0)
        date = result.get('date', 'Unknown')
        print(f"{i:2d}. {name:<40} {resolved:>5.1f}% ({date})")
    
    print(f"\nTotal entries shown: {len(results)}")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard name (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    board_data = get_board_data(data, args.board)
    display_leaderboard(board_data, args.top)


if __name__ == "__main__":
    main()
