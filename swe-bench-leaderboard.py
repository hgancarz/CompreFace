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


def find_board_by_name(data, board_name):
    """Find a specific board by name in the leaderboards array."""
    leaderboards = data.get('leaderboards', [])
    for board in leaderboards:
        if board.get('name') == board_name:
            return board
    return None


def display_leaderboard(board_data, top_n=None):
    """Display the specified leaderboard."""
    if not board_data:
        return False
    
    board_name = board_data.get('name', 'Unknown')
    results = board_data.get('results', [])
    
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 50)
    
    if top_n:
        results = results[:top_n]
    
    for i, entry in enumerate(results, 1):
        name = entry.get('name', 'N/A')
        resolved = entry.get('resolved', 0)
        print(f"{i:2d}. {name:<40} {resolved:.1f}%")
    
    print(f"\nTotal entries: {len(results)}")
    if top_n and len(board_data.get('results', [])) > top_n:
        print(f"Showing top {top_n} entries")
    
    return True


def list_available_boards(data):
    """List all available boards."""
    leaderboards = data.get('leaderboards', [])
    print("Available boards:")
    for board in leaderboards:
        print(f"  - {board.get('name')}")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", help="Leaderboard name (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    parser.add_argument("--list", action="store_true", help="List all available boards")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    
    if args.list:
        list_available_boards(data)
        return
    
    if not args.board:
        print("Error: --board argument is required unless using --list", file=sys.stderr)
        parser.print_help()
        sys.exit(1)
    
    board_data = find_board_by_name(data, args.board)
    if not board_data:
        print(f"Error: Board '{args.board}' not found.", file=sys.stderr)
        list_available_boards(data)
        sys.exit(1)
    
    display_leaderboard(board_data, args.top)


if __name__ == "__main__":
    main()
