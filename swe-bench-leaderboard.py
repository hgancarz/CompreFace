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


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    leaderboards = data.get("leaderboards", [])
    
    # Find the requested board
    board_data = None
    available_boards = []
    for board in leaderboards:
        available_boards.append(board.get("name", "unknown"))
        if board.get("name") == board_name:
            board_data = board
            break
    
    if board_data is None:
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    entries = board_data.get("results", [])
    
    if not entries:
        print(f"No entries found for board '{board_name}'")
        return
    
    # Sort by resolved percentage (descending)
    entries.sort(key=lambda x: x.get("resolved", 0), reverse=True)
    
    if top_n is not None:
        entries = entries[:top_n]
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 70)
    
    for i, entry in enumerate(entries, 1):
        model = entry.get("name", "Unknown")
        resolved = entry.get("resolved", 0)
        cost = entry.get("cost")
        date = entry.get("date", "Unknown")
        
        cost_str = f"Cost: ${cost:.2f}" if cost is not None else "Cost: N/A"
        print(f"{i:2d}. {model:<40} {resolved:>6.1f}%  {cost_str:<15} Date: {date}")


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
        help="Name of the leaderboard to display (e.g., Verified, Lite, Multimodal)"
    )
    
    parser.add_argument(
        "--top",
        type=int,
        help="Show only the top N entries"
    )
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
