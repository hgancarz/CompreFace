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


def find_board_data(data, board_name):
    """Find the specific board data by name."""
    for board in data.get("leaderboards", []):
        if board.get("name") == board_name:
            return board.get("results", [])
    return None


def display_leaderboard(results, board_name, top_n=None):
    """Display the specified leaderboard."""
    if results is None:
        print(f"Error: Board '{board_name}' not found.", file=sys.stderr)
        print(f"Available boards: {[board['name'] for board in data.get('leaderboards', [])]}", file=sys.stderr)
        sys.exit(1)
    
    if not results:
        print(f"No data available for board '{board_name}'")
        return
    
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 50)
    
    # Display the leaderboard entries
    entries = results[:top_n] if top_n else results
    
    for i, entry in enumerate(entries, 1):
        name = entry.get("name", "N/A")
        resolved = entry.get("resolved", "N/A")
        print(f"{i}. {name}: {resolved}% resolved")
    
    if top_n and len(results) > top_n:
        print(f"\n... and {len(results) - top_n} more entries")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries only")
    
    args = parser.parse_args()
    
    # Fetch the leaderboard data
    data = fetch_leaderboard_data()
    
    # Find and display the requested board
    board_results = find_board_data(data, args.board)
    display_leaderboard(board_results, args.board, args.top)


if __name__ == "__main__":
    main()
