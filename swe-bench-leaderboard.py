#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard from GitHub.
"""

import argparse
import json
import sys
from urllib.request import urlopen
from urllib.error import URLError


def fetch_leaderboard():
    """Fetch the leaderboard data from GitHub."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except URLError as e:
        print(f"Error fetching leaderboard: {e}", file=sys.stderr)
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"Error parsing JSON data: {e}", file=sys.stderr)
        sys.exit(1)


def get_board_data(data, board_name):
    """Get data for a specific board."""
    for board in data['leaderboards']:
        if board['name'] == board_name:
            return board
    return None


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    board_data = get_board_data(data, board_name)
    
    if board_data is None:
        available_boards = [board['name'] for board in data['leaderboards']]
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    results = board_data.get('results', [])
    
    if not results:
        print(f"No results found for board '{board_name}'")
        return
    
    if top_n is not None:
        results = results[:top_n]
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    print(f"{'Rank':<4} {'Name':<35} {'Resolved':<10} {'Date':<12}")
    print("-" * 60)
    
    for i, result in enumerate(results, 1):
        name = result.get('name', 'Unknown')
        resolved = result.get('resolved', 0)
        date = result.get('date', 'Unknown')
        print(f"{i:<4} {name:<35} {resolved:<10.1f} {date:<12}")


def main():
    parser = argparse.ArgumentParser(description='Fetch SWE bench leaderboard')
    parser.add_argument('--board', required=True, 
                       help='Leaderboard to display (e.g., Verified, Lite, Multimodal)')
    parser.add_argument('--top', type=int, 
                       help='Show only top N entries')
    
    args = parser.parse_args()
    
    data = fetch_leaderboard()
    display_leaderboard(data, args.board, args.top)


if __name__ == '__main__':
    main()
