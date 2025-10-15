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
    """Fetch leaderboard data from the GitHub URL."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def get_board_data(data, board_name):
    """Get data for a specific board name."""
    for board in data.get("leaderboards", []):
        if board.get("name") == board_name:
            return board.get("results", [])
    return None


def display_leaderboard(results, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    if results is None:
        print(f"Error: Board '{board_name}' not found.", file=sys.stderr)
        sys.exit(1)
    
    if top_n is not None:
        results = results[:top_n]
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    for i, entry in enumerate(results, 1):
        name = entry.get('name', 'N/A')
        resolved = entry.get('resolved', 'N/A')
        if isinstance(resolved, (int, float)):
            resolved_str = f"{resolved:.1f}%"
        else:
            resolved_str = str(resolved)
        
        print(f"{i:2d}. {name:<40} {resolved_str:>10}")
    
    print(f"\nTotal entries shown: {len(results)}")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, 
                       help="Leaderboard type to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, 
                       help="Show top N entries (default: show all)")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    results = get_board_data(data, args.board)
    display_leaderboard(results, args.board, args.top)


if __name__ == "__main__":
    main()
