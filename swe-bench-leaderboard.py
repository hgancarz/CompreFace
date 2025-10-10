#!/usr/bin/env python3
"""
SWE Bench Leaderboard Utility

Fetches and displays the SWE bench leaderboard data from the official GitHub repository.
"""

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard_data():
    """Fetch the leaderboard data from the GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
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
    
    # Sort by resolved percentage (assuming higher is better)
    sorted_results = sorted(results, key=lambda x: x.get('resolved', 0), reverse=True)
    
    # Limit to top N if specified
    if top_n is not None and top_n > 0:
        sorted_results = sorted_results[:top_n]
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    print(f"{'Rank':<4} {'Name':<30} {'Resolved %':<12} {'Date':<10}")
    print("-" * 60)
    
    for i, result in enumerate(sorted_results, 1):
        name = result.get('name', 'Unknown')
        resolved = result.get('resolved', 0)
        date = result.get('date', 'Unknown')
        print(f"{i:<4} {name:<30} {resolved:<12.1f} {date:<10}")


def main():
    parser = argparse.ArgumentParser(description='Fetch SWE Bench leaderboard data')
    parser.add_argument('--board', required=True, 
                       help='Leaderboard type to display')
    parser.add_argument('--top', type=int, 
                       help='Show top N entries (optional)')
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == '__main__':
    main()
