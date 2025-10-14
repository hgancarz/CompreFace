#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display the SWE bench leaderboard data.
"""

import argparse
import json
import requests
import sys


def fetch_leaderboard_data():
    """Fetch the leaderboard data from the GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        response = requests.get(url)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"Error parsing JSON data: {e}", file=sys.stderr)
        sys.exit(1)


def find_board_data(data, board_name):
    """Find the specific board data by name."""
    for board in data['leaderboards']:
        if board['name'] == board_name:
            return board['results']
    return None


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard for the specified board."""
    board_data = find_board_data(data, board_name)
    
    if board_data is None:
        available_boards = [board['name'] for board in data['leaderboards']]
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    if not board_data:
        print(f"No data available for board '{board_name}'")
        return
    
    print(f"\nSWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    # Display the top N entries if specified
    entries = board_data[:top_n] if top_n else board_data
    
    for i, entry in enumerate(entries, 1):
        print(f"{i:2d}. {entry.get('name', 'Unknown')}")
        print(f"    Resolved: {entry.get('resolved', 'N/A')}%")
        print(f"    Date: {entry.get('date', 'N/A')}")
        print(f"    Cost: ${entry.get('cost', 'N/A'):.2f}" if entry.get('cost') else "    Cost: N/A")
        
        # Add any additional fields that might be present
        for key, value in entry.items():
            if key not in ['name', 'resolved', 'date', 'cost', 'logo', 'site', 'folder', 'logs', 'trajs', 'trajs_docent']:
                if value is not None and value != "":
                    print(f"    {key}: {value}")
        
        print()


def main():
    parser = argparse.ArgumentParser(
        description="Fetch and display the SWE bench leaderboard",
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    
    parser.add_argument(
        '--board',
        required=True,
        choices=['bash-only', 'Test', 'Verified', 'Lite', 'Multimodal'],
        help='Which leaderboard to display (bash-only, Test, Verified, Lite, or Multimodal)'
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
