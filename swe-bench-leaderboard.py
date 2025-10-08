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


def display_leaderboard(data, board_name, top_n=None):
    """Display the specified leaderboard."""
    leaderboards = data.get('leaderboards', [])
    available_boards = [board['name'] for board in leaderboards]
    
    board_data = None
    for board in leaderboards:
        if board['name'] == board_name:
            board_data = board
            break
    
    if not board_data:
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    entries = board_data.get('results', [])
    
    if not entries:
        print(f"No entries found for board '{board_name}'")
        return
    
    if top_n:
        entries = entries[:top_n]
    
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 60)
    
    for i, entry in enumerate(entries, 1):
        name = entry.get('name', 'Unknown')
        score = entry.get('resolved', 0)
        print(f"{i:2d}. {name:<40} {score:>6.1f}%")


def main():
    parser = argparse.ArgumentParser(description='Fetch SWE Bench leaderboard data')
    parser.add_argument('--board', required=True, help='Leaderboard name (e.g., Verified, Lite, Multimodal)')
    parser.add_argument('--top', type=int, help='Show top N entries')
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == '__main__':
    main()
