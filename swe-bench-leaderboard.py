#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard data.
"""

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard():
    """Fetch the leaderboard data from GitHub."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board, top=None):
    """Display the leaderboard data for the specified board."""
    # Find the requested board in the leaderboards array
    leaderboard_data = None
    for lb in data.get("leaderboards", []):
        if lb.get("name") == board:
            leaderboard_data = lb
            break
    
    if not leaderboard_data:
        available_boards = [lb.get("name") for lb in data.get("leaderboards", [])]
        print(f"Error: Board '{board}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    results = leaderboard_data.get("results", [])
    
    if top:
        try:
            top = int(top)
            if top <= 0:
                print("Error: --top must be a positive integer", file=sys.stderr)
                sys.exit(1)
            results = results[:top]
        except ValueError:
            print("Error: --top must be a positive integer", file=sys.stderr)
            sys.exit(1)
    
    print(f"SWE Bench Leaderboard - {board}")
    print("=" * 50)
    
    for i, entry in enumerate(results, 1):
        name = entry.get("name", "Unknown")
        score = entry.get("resolved", 0)
        print(f"{i}. {name}: {score}%")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True,
                       help="Leaderboard type to display")
    parser.add_argument("--top", type=int, 
                       help="Show only top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
