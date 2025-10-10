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


def fetch_leaderboard_data() -> Dict[str, Any]:
    """Fetch the leaderboard data from GitHub."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    
    try:
        with urllib.request.urlopen(url) as response:
            data = json.loads(response.read().decode('utf-8'))
            return data
    except urllib.error.URLError as e:
        print(f"Error fetching data: {e}", file=sys.stderr)
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"Error parsing JSON data: {e}", file=sys.stderr)
        sys.exit(1)


def find_board_by_name(leaderboards: List[Dict], board_name: str) -> Dict[str, Any]:
    """Find a specific board by name in the leaderboards list."""
    for board in leaderboards:
        if board.get('name') == board_name:
            return board
    return None


def display_board(board_data: Dict[str, Any], top_n: int = None) -> None:
    """Display the specified board data."""
    if not board_data:
        print("No board data found")
        return
    
    results = board_data.get('results', [])
    
    if not results:
        print(f"No results available for board '{board_data.get('name', 'Unknown')}'")
        return
    
    print(f"\nSWE Bench Leaderboard - {board_data.get('name', 'Unknown')}")
    print("=" * 60)
    
    # Sort by resolved score (descending)
    sorted_results = sorted(results, key=lambda x: x.get('resolved', 0), reverse=True)
    
    if top_n is not None and top_n > 0:
        sorted_results = sorted_results[:top_n]
    
    for i, entry in enumerate(sorted_results, 1):
        name = entry.get('name', 'Unknown')
        score = entry.get('resolved', 0)
        date = entry.get('date', 'Unknown')
        print(f"{i:2d}. {name:<40} {score:>6.1f}%  ({date})")


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
        choices=['Verified', 'Lite', 'Multimodal', 'bash-only', 'Test'],
        help='Which leaderboard to display'
    )
    
    parser.add_argument(
        '--top',
        type=int,
        help='Show only the top N entries'
    )
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    leaderboards = data.get('leaderboards', [])
    board_data = find_board_by_name(leaderboards, args.board)
    
    if not board_data:
        available_boards = [board.get('name') for board in leaderboards]
        print(f"Error: Board '{args.board}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    display_board(board_data, args.top)


if __name__ == "__main__":
    main()
