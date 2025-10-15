#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard from GitHub.
"""

import argparse
import json
import sys
import urllib.request
from typing import Dict, List, Any


def fetch_leaderboard_data(url: str) -> Dict[str, Any]:
    """Fetch leaderboard data from the given URL."""
    try:
        with urllib.request.urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def get_board_data(data: Dict[str, Any], board_name: str) -> List[Dict[str, Any]]:
    """Get the results for a specific board."""
    for board in data.get('leaderboards', []):
        if board.get('name') == board_name:
            return board.get('results', [])
    return []


def display_leaderboard(board_data: List[Dict[str, Any]], board: str, top_n: int = None) -> None:
    """Display the leaderboard for the specified board."""
    if not board_data:
        print(f"No data available for board '{board}'")
        return
    
    print(f"SWE Bench Leaderboard - {board}")
    print("=" * 50)
    
    # Sort by resolved percentage (descending)
    sorted_data = sorted(board_data, key=lambda x: x.get('resolved', 0), reverse=True)
    
    # Display top N entries if specified
    entries = sorted_data[:top_n] if top_n else sorted_data
    
    for i, entry in enumerate(entries, 1):
        name = entry.get('name', 'N/A')
        resolved = entry.get('resolved', 0)
        print(f"{i}. {name}: {resolved:.2f}%")
    
    if top_n and len(sorted_data) > top_n:
        print(f"\n... and {len(sorted_data) - top_n} more entries")


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
        '--board',
        required=True,
        choices=['bash-only', 'Test', 'Verified', 'Lite', 'Multimodal'],
        help='Which leaderboard to display'
    )
    
    parser.add_argument(
        '--top',
        type=int,
        help='Show only the top N entries'
    )
    
    args = parser.parse_args()
    
    # Leaderboard URL from the PR description
    leaderboard_url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    
    # Fetch and display the leaderboard
    data = fetch_leaderboard_data(leaderboard_url)
    board_data = get_board_data(data, args.board)
    
    if not board_data:
        print(f"Error: Board '{args.board}' not found in leaderboard data.", file=sys.stderr)
        available_boards = [board['name'] for board in data.get('leaderboards', [])]
        print(f"Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    display_leaderboard(board_data, args.board, args.top)


if __name__ == "__main__":
    main()
