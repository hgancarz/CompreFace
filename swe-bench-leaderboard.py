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
            data = json.load(response)
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    leaderboards = data.get('leaderboards', [])
    
    # Find the requested board
    board_data = None
    for board in leaderboards:
        if board.get('name') == board_name:
            board_data = board
            break
    
    if not board_data:
        available_boards = [board.get('name') for board in leaderboards]
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    results = board_data.get('results', [])
    
    if not results:
        print(f"No results found for board '{board_name}'")
        return
    
    # Sort by resolved percentage (higher is better)
    sorted_results = sorted(results, key=lambda x: x.get('resolved', 0), reverse=True)
    
    if top_n:
        sorted_results = sorted_results[:top_n]
    
    print(f"\n{board_name} Leaderboard")
    print("=" * 60)
    
    for i, result in enumerate(sorted_results, 1):
        name = result.get('name', 'N/A')
        resolved = result.get('resolved', 'N/A')
        date = result.get('date', 'N/A')
        print(f"{i:2d}. {name}: {resolved}% resolved (Date: {date})")


def main():
    parser = argparse.ArgumentParser(description='Fetch SWE bench leaderboard data')
    parser.add_argument('--board', required=True, help='Board name (e.g., Verified, Lite, Multimodal)')
    parser.add_argument('--top', type=int, help='Show top N entries')
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == '__main__':
    main()
