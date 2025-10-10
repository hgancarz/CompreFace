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
    """Fetch leaderboard data from the GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def find_board_data(data, board_name):
    """Find the specific board data by name."""
    for board in data['leaderboards']:
        if board['name'] == board_name:
            return board['results']
    return None


def display_leaderboard(board_data, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    if not board_data:
        print(f"No data available for board '{board_name}'")
        return
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    # Display top N entries if specified
    entries = board_data[:top_n] if top_n else board_data
    
    for i, entry in enumerate(entries, 1):
        name = entry.get('name', 'N/A')
        resolved = entry.get('resolved', 'N/A')
        total = entry.get('total', 'N/A')
        
        # Format the output
        resolved_str = f"{resolved}%" if isinstance(resolved, (int, float)) else str(resolved)
        total_str = str(total)
        
        print(f"{i:2d}. {name:<40} | Resolved: {resolved_str:<8} | Total: {total_str}")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, 
                       help="Board name (e.g., Verified, Lite, Multimodal, bash-only, Test)")
    parser.add_argument("--top", type=int, 
                       help="Show top N entries (default: show all)")
    
    args = parser.parse_args()
    
    # Fetch leaderboard data
    data = fetch_leaderboard_data()
    
    # Find the requested board
    board_data = find_board_data(data, args.board)
    
    if board_data is None:
        available_boards = [board['name'] for board in data['leaderboards']]
        print(f"Board '{args.board}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    # Display the requested board
    display_leaderboard(board_data, args.board, args.top)


if __name__ == "__main__":
    main()
