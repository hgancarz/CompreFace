#!/usr/bin/env python3
"""
SWE Bench Leaderboard Utility

A Python script to fetch and display the SWE bench leaderboard data.
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


def find_board(data, board_name):
    """Find a specific board by name in the leaderboards data."""
    for board in data.get('leaderboards', []):
        if board.get('name') == board_name:
            return board
    return None


def display_leaderboard(board_data, top_n=None):
    """Display the specified leaderboard with optional top N results."""
    if not board_data or not board_data.get('results'):
        print(f"No data available for this board")
        return
    
    results = board_data['results']
    
    if top_n:
        results = results[:top_n]
    
    print(f"SWE Bench Leaderboard: {board_data['name']}")
    print("=" * 60)
    
    for i, entry in enumerate(results, 1):
        name = entry.get('name', 'Unknown')
        resolved = entry.get('resolved', 'N/A')
        date = entry.get('date', 'N/A')
        print(f"{i:2d}. {name}")
        print(f"     Resolved: {resolved}% | Date: {date}")
        if 'tags' in entry and entry['tags']:
            tags = [tag for tag in entry['tags'] if tag]
            if tags:
                print(f"     Tags: {', '.join(tags[:3])}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch and display SWE bench leaderboard data")
    parser.add_argument("--board", required=True, help="Leaderboard to display (e.g., Verified, Lite, Multimodal)")
    parser.add_argument("--top", type=int, help="Show only top N results")
    
    args = parser.parse_args()
    
    # Fetch the leaderboard data
    data = fetch_leaderboard_data()
    
    # Find the requested board
    board_data = find_board(data, args.board)
    
    if not board_data:
        available_boards = [board['name'] for board in data.get('leaderboards', [])]
        print(f"Error: Board '{args.board}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    # Display the requested board
    display_leaderboard(board_data, args.top)


if __name__ == "__main__":
    main()
