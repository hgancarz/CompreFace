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
    # The data structure has changed - it now contains a list of leaderboards
    if "leaderboards" not in data:
        print("Error: Invalid data format - missing 'leaderboards' key", file=sys.stderr)
        sys.exit(1)
    
    # Find the requested board
    board_data = None
    for board in data["leaderboards"]:
        if board.get("name") == board_name:
            board_data = board
            break
    
    if not board_data:
        available_boards = [b.get("name", "Unknown") for b in data["leaderboards"]]
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    entries = board_data.get("results", [])
    
    if top_n:
        entries = entries[:top_n]
    
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 50)
    
    for i, entry in enumerate(entries, 1):
        model_name = entry.get("name", "Unknown")
        score = entry.get("resolved", 0)
        print(f"{i:2d}. {model_name}: {score:.1f}")


def main():
    parser = argparse.ArgumentParser(description="Fetch and display SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., bash-only, verified, lite, multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
