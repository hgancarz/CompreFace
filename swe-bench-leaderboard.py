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
    """Get the data for a specific board."""
    leaderboards = data.get('leaderboards', [])
    for board in leaderboards:
        if board.get('name') == board_name:
            return board.get('results', [])
    return None


def display_leaderboard(board_data, board_name, top_n=None):
    """Display the leaderboard data for the specified board."""
    if board_data is None:
        print(f"Error: Board '{board_name}' not found.", file=sys.stderr)
        sys.exit(1)
    
    if not board_data:
        print(f"No data available for board '{board_name}'")
        return
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    # Display top N entries if specified
    entries = board_data[:top_n] if top_n else board_data
    
    for i, entry in enumerate(entries, 1):
        print(f"{i:2d}. {entry.get('name', 'Unknown')}")
        print(f"     Resolved: {entry.get('resolved', 'N/A')}%")
        print(f"     Date: {entry.get('date', 'N/A')}")
        print(f"     Cost: ${entry.get('cost', 'N/A'):.2f}" if entry.get('cost') else "     Cost: N/A")
        print(f"     Model: {[tag for tag in entry.get('tags', []) if tag.startswith('Model:')][0] if any(tag.startswith('Model:') for tag in entry.get('tags', [])) else 'Unknown'}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard name (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    board_data = get_board_data(data, args.board)
    display_leaderboard(board_data, args.board, args.top)


if __name__ == "__main__":
    main()
