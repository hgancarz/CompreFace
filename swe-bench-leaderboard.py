#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard from GitHub.
"""

import argparse
import json
import sys
from urllib.request import urlopen
from urllib.error import URLError, HTTPError


def fetch_leaderboard_data():
    """Fetch the leaderboard JSON data from GitHub."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode('utf-8'))
        return data
    except HTTPError as e:
        print(f"HTTP Error {e.code}: {e.reason}", file=sys.stderr)
        sys.exit(1)
    except URLError as e:
        print(f"URL Error: {e.reason}", file=sys.stderr)
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"JSON Decode Error: {e}", file=sys.stderr)
        sys.exit(1)


def get_available_boards(data):
    """Get list of available board names."""
    return [board['name'] for board in data['leaderboards']]


def find_board_data(data, board_name):
    """Find the board data for the specified board name."""
    for board in data['leaderboards']:
        if board['name'].lower() == board_name.lower():
            return board
    return None


def display_leaderboard(data, board_name, top_n=None):
    """Display the specified leaderboard."""
    board_data = find_board_data(data, board_name)
    
    if not board_data:
        available_boards = get_available_boards(data)
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    results = board_data['results']
    
    if not results:
        print(f"No data available for board '{board_name}'")
        return
    
    # Sort by resolved percentage (highest first)
    sorted_results = sorted(results, key=lambda x: x.get('resolved', 0), reverse=True)
    
    # Get the entries to display
    entries = sorted_results[:top_n] if top_n else sorted_results
    
    print(f"\nSWE Bench Leaderboard - {board_data['name']}")
    print("=" * 60)
    
    for i, entry in enumerate(entries, 1):
        name = entry.get('name', 'Unknown')
        resolved = entry.get('resolved', 'N/A')
        cost = entry.get('cost', 'N/A')
        date = entry.get('date', 'N/A')
        
        print(f"{i}. {name}")
        print(f"   Resolved: {resolved}%")
        if cost != 'N/A' and cost is not None:
            print(f"   Cost: ${cost:.2f}")
        print(f"   Date: {date}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch and display SWE bench leaderboards")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., verified, lite, bash-only)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
