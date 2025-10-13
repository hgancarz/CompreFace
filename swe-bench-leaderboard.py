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
    """Fetch leaderboard data from GitHub."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def find_board_by_name(data, board_name):
    """Find a board by name (case-insensitive)."""
    boards = data.get('leaderboards', [])
    for board in boards:
        if board.get('name', '').lower() == board_name.lower():
            return board
    return None


def safe_format(value, format_spec, default="N/A"):
    """Safely format a value, handling None."""
    if value is None:
        return default
    try:
        return format(value, format_spec)
    except (ValueError, TypeError):
        return str(value)[:len(default)]


def display_leaderboard(board_data, board_name, top_n=None):
    """Display the leaderboard for a specific board."""
    entries = board_data.get('results', [])
    
    if not entries:
        print(f"No entries found for board '{board_name}'")
        return
    
    # Sort by resolved score (assuming higher is better)
    sorted_entries = sorted(entries, key=lambda x: x.get('resolved', 0) or 0, reverse=True)
    
    # Apply top N filter if specified
    if top_n is not None:
        sorted_entries = sorted_entries[:top_n]
    
    # Display header
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    print(f"{'Rank':<4} {'Name':<30} {'Resolved':<10} {'Date':<12} {'Cost':<10}")
    print("-" * 60)
    
    # Display entries
    for i, entry in enumerate(sorted_entries, 1):
        name = entry.get('name', 'Unknown')[:28]
        resolved = entry.get('resolved')
        date = entry.get('date', 'Unknown')[:10]
        cost = entry.get('cost')
        
        resolved_str = safe_format(resolved, ".1f", "N/A")
        cost_str = safe_format(cost, ".2f", "N/A")
        
        print(f"{i:<4} {name:<30} {resolved_str:<10} {date:<12} ${cost_str:<9}")


def main():
    parser = argparse.ArgumentParser(description='Fetch SWE bench leaderboard data')
    parser.add_argument('--board', required=True, 
                       help='Leaderboard type to display (verified, lite, multimodal, bash-only)')
    parser.add_argument('--top', type=int, 
                       help='Show only top N entries')
    
    args = parser.parse_args()
    
    # Fetch data
    data = fetch_leaderboard_data()
    
    # Find the requested board
    board_data = find_board_by_name(data, args.board)
    if not board_data:
        available_boards = [b.get('name', 'Unknown') for b in data.get('leaderboards', [])]
        print(f"Error: Board '{args.board}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    # Display requested board
    display_leaderboard(board_data, board_data['name'], args.top)


if __name__ == '__main__':
    main()
