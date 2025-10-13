#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display SWE bench leaderboard data.
"""

import argparse
import json
import sys
import urllib.request
from typing import Dict, List, Any


def fetch_leaderboard_data() -> Dict[str, Any]:
    """Fetch leaderboard data from the SWE bench GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    
    try:
        with urllib.request.urlopen(url) as response:
            data = json.loads(response.read().decode('utf-8'))
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def find_board_by_name(data: Dict[str, Any], board_name: str) -> Dict[str, Any]:
    """Find a specific board by name in the leaderboards data."""
    for board in data.get('leaderboards', []):
        if board.get('name') == board_name:
            return board
    return None


def display_leaderboard(board_data: Dict[str, Any], top: int = None) -> None:
    """Display the specified leaderboard."""
    if not board_data:
        print("Error: Board data not found", file=sys.stderr)
        sys.exit(1)
    
    results = board_data.get('results', [])
    
    print(f"\n=== SWE Bench Leaderboard: {board_data.get('name', 'Unknown')} ===\n")
    
    # Display top N entries if specified
    entries = results
    if top and top > 0:
        entries = entries[:top]
    
    for i, entry in enumerate(entries, 1):
        print(f"{i}. {entry.get('name', 'Unknown')}")
        if 'resolved' in entry:
            print(f"   Resolved: {entry['resolved']}%")
        if 'date' in entry:
            print(f"   Date: {entry['date']}")
        if entry.get('cost') is not None:
            print(f"   Cost: ${entry['cost']:.2f}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    board_data = find_board_by_name(data, args.board)
    
    if not board_data:
        available_boards = [board['name'] for board in data.get('leaderboards', [])]
        print(f"Error: Board '{args.board}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    display_leaderboard(board_data, args.top)


if __name__ == "__main__":
    main()
