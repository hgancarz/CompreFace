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
    """Fetch leaderboard data from the GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def find_board_by_name(data, board_name):
    """Find a board by name in the leaderboards data."""
    for board in data.get("leaderboards", []):
        if board.get("name", "").lower() == board_name.lower():
            return board
    return None


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard data for the specified board."""
    board_data = find_board_by_name(data, board_name)
    
    if not board_data:
        print(f"Error: Board '{board_name}' not found in leaderboard data", file=sys.stderr)
        available_boards = [board.get("name") for board in data.get("leaderboards", [])]
        print(f"Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    results = board_data.get("results", [])
    
    if top_n is not None:
        results = results[:top_n]
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    for i, entry in enumerate(results, 1):
        name = entry.get("name", "Unknown")
        resolved = entry.get("resolved", 0)
        print(f"{i:2d}. {name}: {resolved}% resolved")
    
    if top_n and len(board_data.get("results", [])) > top_n:
        remaining = len(board_data.get("results", [])) - top_n
        print(f"\n... and {remaining} more entries")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard name (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
