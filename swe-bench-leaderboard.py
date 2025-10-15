#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard data.
"""

import argparse
import json
import sys
import urllib.request
from typing import Dict, List, Any


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


def display_leaderboard(data: Dict[str, Any], board_name: str, top: int = None) -> None:
    """Display the specified leaderboard."""
    # Find the requested board
    board = None
    for b in data['leaderboards']:
        if b['name'] == board_name:
            board = b
            break
    
    if not board:
        available_boards = [b['name'] for b in data['leaderboards']]
        print(f"Error: Board '{board_name}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    results = board['results']
    
    print(f"\nSWE Bench Leaderboard: {board_name}")
    print("=" * 50)
    
    if top:
        results = results[:top]
    
    for i, entry in enumerate(results, 1):
        print(f"{i:2d}. {entry.get('name', 'Unknown')}")
        print(f"    Resolved: {entry.get('resolved', 'N/A')}%")
        model_tag = entry.get('tags', ['N/A'])[0] if entry.get('tags') else 'N/A'
        print(f"    Model: {model_tag}")
        if 'date' in entry:
            print(f"    Date:  {entry['date']}")
        if entry.get('cost') is not None:
            print(f"    Cost:  ${entry['cost']:.2f}")
        print()


def main():
    parser = argparse.ArgumentParser(
        description="Fetch and display SWE bench leaderboard data",
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    
    parser.add_argument(
        '--board',
        required=True,
        help='Which leaderboard to display (Verified, Lite, Multimodal, bash-only, Test)'
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
