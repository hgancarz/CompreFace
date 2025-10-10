#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard from GitHub.
"""

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard():
    """Fetch the leaderboard data from GitHub."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    # Find the requested board
    board_data = None
    available_boards = []
    
    for board in data["leaderboards"]:
        available_boards.append(board["name"])
        if board["name"] == board_name:
            board_data = board["results"]
            break
    
    if board_data is None:
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    if top_n:
        board_data = board_data[:top_n]
        print(f"Top {top_n} entries:")
    
    for i, entry in enumerate(board_data, 1):
        name = entry.get("name", "Unknown")
        score = entry.get("resolved", 0)
        print(f"{i:2d}. {name}: {score}%")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard")
    parser.add_argument("--board", required=True, 
                       help="Leaderboard type to display")
    parser.add_argument("--top", type=int, 
                       help="Show top N entries (optional)")
    
    args = parser.parse_args()
    
    # Fetch leaderboard data
    data = fetch_leaderboard()
    
    # Display the requested board
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
