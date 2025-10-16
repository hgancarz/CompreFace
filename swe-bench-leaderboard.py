#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display SWE bench leaderboard data.
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


def display_leaderboard(data, board_name, top_n=None):
    """Display leaderboard data for a specific board."""
    # The data structure has a "leaderboards" key containing an array of board objects
    if "leaderboards" not in data:
        print("Error: Invalid data structure - missing \"leaderboards\" key", file=sys.stderr)
        sys.exit(1)
    
    # Find the requested board
    board_data = None
    for board in data["leaderboards"]:
        if board["name"].lower() == board_name.lower():
            board_data = board
            break
    
    if not board_data:
        available_boards = [board["name"] for board in data["leaderboards"]]
        print(f"Error: Board \"{board_name}\" not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    results = board_data.get("results", [])
    
    if not results:
        print(f"No data available for board \"{board_name}\"")
        return
    
    # Sort by resolved score (descending)
    sorted_results = sorted(results, key=lambda x: x.get("resolved", 0), reverse=True)
    
    # Display the leaderboard
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    entries = sorted_results[:top_n] if top_n else sorted_results
    
    for i, entry in enumerate(entries, 1):
        name = entry.get("name", "Unknown")
        score = entry.get("resolved", "N/A")
        print(f"{i}. {name}: {score}")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard name (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    # Fetch data
    data = fetch_leaderboard_data()
    
    # Display requested board
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
