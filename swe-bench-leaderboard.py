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
    """Fetch the leaderboard data from the SWE bench GitHub repository."""
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
    board = None
    available_boards = []
    for b in data["leaderboards"]:
        available_boards.append(b["name"])
        if b["name"] == board_name:
            board = b
            break
    
    if not board:
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    results = board["results"]
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 50)
    
    if top_n:
        results = results[:top_n]
        print(f"Top {top_n} entries:")
    
    for i, entry in enumerate(results, 1):
        name = entry.get("name", "N/A")
        resolved = entry.get("resolved", "N/A")
        print(f"{i}. {name}: {resolved}%")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
