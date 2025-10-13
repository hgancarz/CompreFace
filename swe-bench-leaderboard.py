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


def get_available_boards(data):
    """Get list of available board names."""
    return [board["name"] for board in data.get("leaderboards", [])]


def find_board_data(data, board_name):
    """Find data for a specific board."""
    for board in data.get("leaderboards", []):
        if board["name"].lower() == board_name.lower():
            return board
    return None


def display_leaderboard(board_data, top_n=None):
    """Display the leaderboard for a specific board."""
    if not board_data:
        print("No board data found")
        return
    
    results = board_data.get("results", [])
    
    if not results:
        print(f"No results available for board '{board_data.get('name', 'Unknown')}'")
        return
    
    print(f"\nSWE Bench Leaderboard - {board_data.get('name', 'Unknown').upper()}")
    print("=" * 60)
    
    # Display top N entries if specified
    entries = results[:top_n] if top_n else results
    
    for i, entry in enumerate(entries, 1):
        print(f"{i}. {entry.get('name', 'Unknown')}")
        print(f"   Resolved: {entry.get('resolved', 'N/A')}%")
        print(f"   Model: {entry.get('tags', ['N/A'])[0] if entry.get('tags') else 'N/A'}")
        print(f"   Date: {entry.get('date', 'N/A')}")
        print(f"   Cost: ${entry.get('cost', 'N/A')}")
        if entry.get('folder'):
            print(f"   Folder: {entry.get('folder')}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Board name (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    board_data = find_board_data(data, args.board)
    
    if not board_data:
        available_boards = get_available_boards(data)
        print(f"Error: Board '{args.board}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    display_leaderboard(board_data, args.top)


if __name__ == "__main__":
    main()
