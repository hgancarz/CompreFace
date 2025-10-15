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


def get_board_data(data, board_name):
    """Get data for a specific board by name."""
    for board in data.get('leaderboards', []):
        if board.get('name') == board_name:
            return board
    return None


def display_leaderboard(board_data, top=None):
    """Display the leaderboard for a specific board."""
    if not board_data:
        return False
    
    entries = board_data.get('results', [])
    
    if top is not None:
        entries = entries[:top]
    
    print(f"SWE Bench Leaderboard - {board_data.get('name', 'Unknown')}")
    print("=" * 50)
    
    for i, entry in enumerate(entries, 1):
        name = entry.get('name', 'Unknown')
        resolved = entry.get('resolved', 'N/A')
        print(f"{i}. {name}")
        print(f"   Resolved: {resolved}%")
        print(f"   Date: {entry.get('date', 'N/A')}")
        if entry.get('cost'):
            print(f"   Cost: ${entry.get('cost'):.2f}")
        print()


def main():
    parser = argparse.ArgumentParser(description='Fetch SWE bench leaderboard data')
    parser.add_argument('--board', required=True,
                       help='Leaderboard type to display (Verified, Lite, Multimodal, etc.)')
    parser.add_argument('--top', type=int, 
                       help='Show only top N entries')
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    board_data = get_board_data(data, args.board)
    
    if not board_data:
        available_boards = [lb['name'] for lb in data.get('leaderboards', [])]
        print(f"Error: Board '{args.board}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    display_leaderboard(board_data, args.top)


if __name__ == '__main__':
    main()
