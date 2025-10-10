#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard data.
"""

import argparse
import json
import sys
from urllib.request import urlopen
from urllib.error import URLError


def fetch_leaderboard_data():
    """Fetch leaderboard data from the GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except URLError as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"Error parsing JSON data: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    # Find the requested board in the leaderboards array
    board_data = None
    available_boards = []
    
    for board in data['leaderboards']:
        available_boards.append(board['name'])
        if board['name'] == board_name:
            board_data = board
            break
    
    if board_data is None:
        print(f"Error: Board '{board_name}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    entries = board_data.get('results', [])
    
    if top_n is not None:
        entries = entries[:top_n]
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    if not entries:
        print("No entries found for this board.")
        return
    
    # Display the leaderboard
    for i, entry in enumerate(entries, 1):
        name = entry.get('name', 'Unknown')
        resolved = entry.get('resolved', 0)
        date = entry.get('date', 'Unknown')
        print(f"{i:2d}. {name:<40} {resolved:>6.1f}%  ({date})")


def main():
    parser = argparse.ArgumentParser(description='Fetch SWE Bench leaderboard data')
    parser.add_argument('--board', required=True,
                       help='Leaderboard type to display')
    parser.add_argument('--top', type=int, 
                       help='Show only top N entries')
    
    args = parser.parse_args()
    
    # Fetch data
    data = fetch_leaderboard_data()
    
    # Display the requested board
    display_leaderboard(data, args.board, args.top)


if __name__ == '__main__':
    main()
