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
    """Display the leaderboard for a specific board."""
    # Find the requested board in the leaderboards list
    board_data = None
    available_boards = []
    
    for board in data.get("leaderboards", []):
        available_boards.append(board.get("name", "unknown"))
        if board.get("name", "").lower() == board_name.lower():
            board_data = board
            break
    
    if board_data is None:
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    entries = board_data.get('results', [])
    
    # Sort by resolved score (descending)
    entries.sort(key=lambda x: x.get('resolved', 0), reverse=True)
    
    if top_n is not None:
        entries = entries[:top_n]
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    if not entries:
        print("No entries found for this board.")
        return
    
    # Display header
    print(f"{'Rank':<6} {'Model':<30} {'Resolved':<10} {'Date':<12}")
    print("-" * 60)
    
    # Display entries
    for i, entry in enumerate(entries, 1):
        model = entry.get('name', 'Unknown')
        resolved = entry.get('resolved', 0)
        date = entry.get('date', 'Unknown')
        print(f"{i:<6} {model:<30} {resolved:<10.1f} {date:<12}")


def main():
    parser = argparse.ArgumentParser(description='Fetch SWE bench leaderboard data')
    parser.add_argument('--board', required=True,
                       help='Leaderboard type to display (e.g., Verified, Lite, Multimodal)')
    parser.add_argument('--top', type=int, 
                       help='Show top N entries (default: show all)')
    
    args = parser.parse_args()
    
    # Fetch data
    data = fetch_leaderboard_data()
    
    # Display the requested board
    display_leaderboard(data, args.board, args.top)


if __name__ == '__main__':
    main()
