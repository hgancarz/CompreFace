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
    """Get the specific board data by name."""
    for board in data['leaderboards']:
        if board['name'] == board_name:
            return board['results']
    return None


def display_leaderboard(results, board_name, top_n=None):
    """Display the specified leaderboard."""
    if not results:
        print(f"No data available for board '{board_name}'")
        return
    
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 60)
    
    # Display entries
    entries = results[:top_n] if top_n else results
    
    for i, entry in enumerate(entries, 1):
        name = entry.get('name', 'N/A')
        resolved = entry.get('resolved', 'N/A')
        print(f"{i:2d}. {name}")
        print(f"    Resolved: {resolved}%")
        if entry.get('date'):
            print(f"    Date: {entry['date']}")
        if entry.get('folder'):
            print(f"    Folder: {entry['folder']}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    results = get_board_data(data, args.board)
    
    if results is None:
        available_boards = [board['name'] for board in data['leaderboards']]
        print(f"Error: Board '{args.board}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    display_leaderboard(results, args.board, args.top)


if __name__ == "__main__":
    main()
