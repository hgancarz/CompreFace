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
    """Fetch the leaderboard data from the SWE bench GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def get_board_data(data, board_name):
    """Get the specific board data by name."""
    for board in data.get("leaderboards", []):
        if board.get("name") == board_name:
            return board.get("results", [])
    return None


def display_leaderboard(board_data, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    if not board_data:
        print(f"No data available for board '{board_name}'")
        return
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    # Display top N entries if specified
    entries = board_data[:top_n] if top_n else board_data
    
    for i, entry in enumerate(entries, 1):
        name = entry.get('name', 'Unknown')
        resolved = entry.get('resolved', 'N/A')
        print(f"{i}. {name}: {resolved}% resolved")
    
    if top_n and len(board_data) > top_n:
        print(f"\n... and {len(board_data) - top_n} more entries")


def main():
    parser = argparse.ArgumentParser(
        description="Fetch and display SWE bench leaderboard data",
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    
    parser.add_argument(
        "--board",
        required=True,
        help="Name of the leaderboard to display (e.g., Verified, Lite, Multimodal, bash-only, Test)"
    )
    
    parser.add_argument(
        "--top",
        type=int,
        help="Show only the top N entries"
    )
    
    args = parser.parse_args()
    
    # Fetch leaderboard data
    data = fetch_leaderboard_data()
    
    # Get the specific board data
    board_data = get_board_data(data, args.board)
    
    if board_data is None:
        available_boards = [board.get("name") for board in data.get("leaderboards", [])]
        print(f"Error: Board '{args.board}' not found in leaderboard data.", file=sys.stderr)
        print(f"Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    # Display the requested board
    display_leaderboard(board_data, args.board, args.top)


if __name__ == "__main__":
    main()
