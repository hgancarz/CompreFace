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
    # Find the requested board
    board_data = None
    for board in data["leaderboards"]:
        if board["name"] == board_name:
            board_data = board["results"]
            break
    
    if board_data is None:
        available_boards = [board["name"] for board in data["leaderboards"]]
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    if top_n:
        board_data = board_data[:top_n]
    
    for i, entry in enumerate(board_data, 1):
        score = entry.get("resolved", 0)
        name = entry.get("name", "Unknown")
        print(f"{i}. {name}: {score}")
    
    print(f"\nTotal entries shown: {len(board_data)}")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, 
                       help="Leaderboard type to display")
    parser.add_argument("--top", type=int, 
                       help="Show top N entries (default: show all)")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
