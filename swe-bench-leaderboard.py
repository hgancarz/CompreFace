#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard from GitHub.
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
            data = json.load(response)
        return data
    except Exception as e:
        print(f"Error fetching leaderboard: {e}", file=sys.stderr)
        sys.exit(1)


def find_board_by_name(data, board_name):
    """Find a specific board by name in the leaderboards array (case-insensitive)."""
    leaderboards = data.get("leaderboards", [])
    board_name_lower = board_name.lower()
    for board in leaderboards:
        if board.get("name", "").lower() == board_name_lower:
            return board
    return None


def display_board(board_data, board_name, top_n=None):
    """Display the specified leaderboard."""
    if not board_data:
        print(f"Error: Board '{board_name}' not found.", file=sys.stderr)
        return False
    
    results = board_data.get("results", [])
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    if top_n:
        results = results[:top_n]
    
    for i, entry in enumerate(results, 1):
        name = entry.get("name", "Unknown")
        resolved = entry.get("resolved", "N/A")
        date = entry.get("date", "N/A")
        cost = entry.get("cost", "N/A")
        
        print(f"{i}. {name}")
        print(f"   Resolved: {resolved}%")
        print(f"   Date: {date}")
        print(f"   Cost: ${cost:.2f}" if isinstance(cost, (int, float)) else f"   Cost: {cost}")
        print()
    
    return True


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard")
    parser.add_argument("--board", required=True, 
                       help="Leaderboard type to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, 
                       help="Show top N entries (optional)")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard()
    
    # Try to find the board by the provided name (case-insensitive)
    board_data = find_board_by_name(data, args.board)
    
    if not board_data:
        # If not found, show available boards
        leaderboards = data.get("leaderboards", [])
        available_boards = [board.get("name") for board in leaderboards]
        print(f"Error: Board '{args.board}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    display_board(board_data, args.board, args.top)


if __name__ == "__main__":
    main()
