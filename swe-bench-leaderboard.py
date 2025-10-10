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
    """Display the specified leaderboard with optional top N results."""
    leaderboards = data.get('leaderboards', [])
    
    # Find the requested board
    board_data = None
    for lb in leaderboards:
        if lb.get('name') == board_name:
            board_data = lb.get('results', [])
            break
    
    if board_data is None:
        available_boards = [lb.get('name') for lb in leaderboards if lb.get('name')]
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    # Sort by resolved percentage (descending)
    board_data = sorted(board_data, key=lambda x: x.get('resolved', 0) or 0, reverse=True)
    
    if top_n is not None:
        board_data = board_data[:top_n]
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    for i, entry in enumerate(board_data, 1):
        print(f"{i:2d}. {entry.get('name', 'N/A')}")
        
        # Handle resolved value
        resolved = entry.get('resolved')
        if resolved is not None:
            print(f"    Resolved: {resolved:.1f}%")
        else:
            print(f"    Resolved: N/A")
            
        print(f"    Date: {entry.get('date', 'N/A')}")
        
        # Show tags if available
        tags = entry.get('tags', [])
        if tags:
            print(f"    Tags: {', '.join(tags[:3])}{'...' if len(tags) > 3 else ''}")
            
        print()


def main():
    parser = argparse.ArgumentParser(
        description="Fetch and display SWE bench leaderboard data",
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    
    parser.add_argument(
        '--board',
        required=True,
        help='Which leaderboard to display (Verified, Lite, Multimodal, etc.)'
    )
    
    parser.add_argument(
        '--top',
        type=int,
        help='Show only the top N entries'
    )
    
    args = parser.parse_args()
    
    # Fetch leaderboard data
    data = fetch_leaderboard_data()
    
    # Display the requested board
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
