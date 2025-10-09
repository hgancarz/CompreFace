#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard from GitHub.
"""

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard_data():
    """Fetch leaderboard data from GitHub."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def get_available_boards(data):
    """Get list of available board names."""
    boards = data.get("leaderboards", [])
    return [board["name"] for board in boards]


def find_board(data, board_name):
    """Find a specific board by name."""
    boards = data.get("leaderboards", [])
    for board in boards:
        if board.get("name") == board_name:
            return board
    return None


def display_leaderboard(board_data, top_n=None):
    """Display the specified leaderboard."""
    if not board_data:
        print("Error: Board data not found", file=sys.stderr)
        sys.exit(1)
    
    results = board_data.get("results", [])
    
    if not results:
        print(f"No results found for board '{board_data.get('name', 'Unknown')}'")
        return
    
    if top_n:
        results = results[:top_n]
    
    print(f"\n{board_data.get('name', 'Unknown')} Leaderboard:")
    print("-" * 60)
    
    for i, result in enumerate(results, 1):
        name = result.get("name", "Unknown")
        resolved = result.get("resolved", 0)
        date = result.get("date", "Unknown")
        print(f"{i:2d}. {name}: {resolved}% (Date: {date})")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard name (e.g., bash-only, lite, multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries (default: show all)")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    available_boards = get_available_boards(data)
    
    board_data = find_board(data, args.board)
    if not board_data:
        print(f"Error: Board '{args.board}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    display_leaderboard(board_data, args.top)


if __name__ == "__main__":
    main()
