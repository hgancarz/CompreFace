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
    """Fetch leaderboard data from the GitHub repository."""
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
    boards = data.get('leaderboards', [])
    board_data = None
    
    # Find the requested board by name
    for board in boards:
        if board.get('name') == board_name:
            board_data = board.get('results', [])
            break
    
    if board_data is None:
        available_boards = [b.get('name') for b in boards if b.get('name')]
        print(f"Error: Board '{board_name}' not found in leaderboard data.", file=sys.stderr)
        print(f"Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    if top_n:
        board_data = board_data[:top_n]
        print(f"Top {top_n} entries:")
    
    for i, entry in enumerate(board_data, 1):
        name = entry.get('name', 'Unknown')
        resolved = entry.get('resolved', 0)
        print(f"{i:2d}. {name}: {resolved:.1f}%")


def main():
    parser = argparse.ArgumentParser(
        description="Fetch and display SWE bench leaderboard data",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python swe-bench-leaderboard.py --board Verified --top 10
  python swe-bench-leaderboard.py --board Lite
  python swe-bench-leaderboard.py --board Multimodal
        """
    )
    
    parser.add_argument(
        "--board",
        required=True,
        help="Leaderboard to display (e.g., Verified, Lite, Multimodal)"
    )
    
    parser.add_argument(
        "--top",
        type=int,
        help="Show only the top N entries"
    )
    
    args = parser.parse_args()
    
    # Fetch and display the leaderboard
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
