#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard data.
"""

import argparse
import json
import sys
import urllib.request
from typing import Dict, Any, List


def fetch_leaderboard_data() -> Dict[str, Any]:
    """Fetch the leaderboard data from the GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    
    try:
        with urllib.request.urlopen(url) as response:
            data = json.loads(response.read().decode('utf-8'))
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def get_board_data(data: Dict[str, Any], board_name: str) -> List[Dict[str, Any]]:
    """Get the specific board data by name."""
    for board in data.get('leaderboards', []):
        if board.get('name') == board_name:
            return board.get('results', [])
    return []


def display_leaderboard(data: Dict[str, Any], board: str, top: int = None) -> None:
    """Display the specified leaderboard."""
    board_data = get_board_data(data, board)
    
    if not board_data:
        available_boards = [b['name'] for b in data.get('leaderboards', [])]
        print(f"Error: Board '{board}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    print(f"\n=== SWE Bench Leaderboard: {board} ===\n")
    
    # Sort by resolved score (descending)
    sorted_data = sorted(board_data, key=lambda x: x.get('resolved', 0), reverse=True)
    
    # Apply top limit if specified
    if top is not None and top > 0:
        sorted_data = sorted_data[:top]
    
    # Display the leaderboard
    for i, entry in enumerate(sorted_data, 1):
        name = entry.get('name', 'Unknown')
        resolved = entry.get('resolved', 0)
        print(f"{i:3d}. {name}: {resolved}%")


def main():
    parser = argparse.ArgumentParser(
        description="Fetch and display SWE bench leaderboard data",
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    
    parser.add_argument(
        '--board',
        required=True,
        help='Which leaderboard to display'
    )
    
    parser.add_argument(
        '--top',
        type=int,
        help='Show only the top N entries'
    )
    
    args = parser.parse_args()
    
    # Fetch the leaderboard data
    data = fetch_leaderboard_data()
    
    # Display the requested board
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
