#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard from GitHub.
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


def get_leaderboard_by_name(data, board_name):
    """Get a specific leaderboard by name."""
    for leaderboard in data['leaderboards']:
        if leaderboard['name'] == board_name:
            return leaderboard['results']
    return None


def safe_format(value, format_spec, default=""):
    """Safely format a value that might be None."""
    if value is None:
        return default
    return format(value, format_spec)


def display_leaderboard(data, board_name, top_n=None):
    """Display the specified leaderboard."""
    results = get_leaderboard_by_name(data, board_name)
    
    if not results:
        available_boards = [lb['name'] for lb in data['leaderboards']]
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    if top_n:
        results = results[:top_n]
    
    for i, entry in enumerate(results, 1):
        name = entry.get('name', 'N/A')
        resolved = entry.get('resolved', 0)
        total = entry.get('instance_calls', 0)
        cost = entry.get('cost', 0)
        
        print(f"{i:2d}. {safe_format(name, '<40')} "
              f"Resolved: {safe_format(resolved, '.1f')}% "
              f"Instances: {safe_format(total, '.0f', 'N/A')} "
              f"Cost: ${safe_format(cost, '.2f', 'N/A')}")


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, 
                       help="Leaderboard type to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, 
                       help="Show top N entries (default: show all)")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
