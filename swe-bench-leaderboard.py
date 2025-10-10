#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard.
"""

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard_data():
    """Fetch the leaderboard data from the SWE bench GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard for the specified board."""
    leaderboards = data.get('leaderboards', [])
    
    # Find the requested board
    board_data = None
    for board in leaderboards:
        if board.get('name') == board_name:
            board_data = board
            break
    
    if board_data is None:
        available_boards = [board.get('name') for board in leaderboards]
        print(f"Error: Board '{board_name}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    results = board_data.get('results', [])
    
    if not results:
        print(f"No results found for board '{board_name}'")
        return
    
    # Apply top N filter if specified
    if top_n is not None:
        results = results[:top_n]
    
    # Display the leaderboard
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    for i, result in enumerate(results, 1):
        name = result.get('name', 'N/A')
        resolved = result.get('resolved', 'N/A')
        date = result.get('date', 'N/A')
        print(f"{i:2d}. {name}")
        print(f"    Resolved: {resolved}% | Date: {date}")
        print()


def main():
    parser = argparse.ArgumentParser(
        description="Fetch and display the SWE bench leaderboard",
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    
    parser.add_argument(
        '--board',
        required=True,
        help='Specify which leaderboard to display'
    )
    
    parser.add_argument(
        '--top',
        type=int,
        help='Show only the top N entries'
    )
    
    args = parser.parse_args()
    
    # Fetch the leaderboard data
    data = fetch_leaderboard_data()
    
    # Display the requested board
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
