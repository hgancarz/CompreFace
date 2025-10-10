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


def display_leaderboard(data, board_name, top_n=None):
    """Display the specified leaderboard."""
    leaderboards = data.get("leaderboards", [])
    
    # Find the requested board
    board_data = None
    for board in leaderboards:
        if board.get("name") == board_name:
            board_data = board
            break
    
    if not board_data:
        available_boards = [board.get("name") for board in leaderboards]
        print(f"Error: Board '{board_name}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    results = board_data.get("results", [])
    print(f"\n=== SWE Bench Leaderboard: {board_name} ===\n")
    
    if top_n:
        results = results[:top_n]
        print(f"Showing top {top_n} entries:\n")
    
    for i, entry in enumerate(results, 1):
        print(f"{i}. {entry.get('name', 'Unknown')}")
        print(f"   Resolved: {entry.get('resolved', 'N/A')}%")
        print(f"   Date: {entry.get('date', 'N/A')}")
        print(f"   Cost: ${entry.get('cost', 'N/A'):.2f}" if entry.get('cost') else "   Cost: N/A")
        if entry.get('site'):
            print(f"   Site: {entry.get('site')}")
        if entry.get('tags'):
            print(f"   Tags: {', '.join(entry.get('tags', []))}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch and display SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show only top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
