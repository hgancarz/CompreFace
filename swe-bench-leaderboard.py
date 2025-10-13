#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard.
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
        print(f"Error fetching leaderboard: {e}", file=sys.stderr)
        sys.exit(1)


def get_available_boards(data):
    """Get list of available board names."""
    return [board["name"] for board in data["leaderboards"]]


def find_board_data(data, board_name):
    """Find the specific board data by name."""
    for board in data["leaderboards"]:
        if board["name"] == board_name:
            return board
    return None


def display_leaderboard(data, board_name, top=None):
    """Display the leaderboard for the specified board."""
    board_data = find_board_data(data, board_name)
    
    if not board_data:
        available_boards = get_available_boards(data)
        print(f"Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    if top:
        try:
            top = int(top)
            if top <= 0:
                print("--top must be a positive integer", file=sys.stderr)
                sys.exit(1)
        except ValueError:
            print("--top must be a positive integer", file=sys.stderr)
            sys.exit(1)
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    entries = board_data.get("results", [])
    
    if top:
        entries = entries[:top]
    
    for i, entry in enumerate(entries, 1):
        name = entry.get("name", "N/A")
        score = entry.get("resolved", "N/A")
        print(f"{i}. {name}: {score}")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard")
    parser.add_argument("--board", required=True, 
                       help="Leaderboard type (e.g., Verified, Lite, Multimodal, bash-only, Test)")
    parser.add_argument("--top", type=int, 
                       help="Show top N entries (optional)")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
