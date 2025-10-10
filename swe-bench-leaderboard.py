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
    """Fetch the leaderboard data from the GitHub URL."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    
    try:
        with urllib.request.urlopen(url) as response:
            data = json.loads(response.read().decode('utf-8'))
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def find_leaderboard(data: Dict[str, Any], board_name: str) -> Dict[str, Any]:
    """Find a specific leaderboard by name."""
    leaderboards = data.get("leaderboards", [])
    for board in leaderboards:
        if board.get("name") == board_name:
            return board
    return None


def display_leaderboard(board_data: Dict[str, Any], top: int = None) -> None:
    """Display the specified leaderboard with optional top N results."""
    if not board_data:
        print("No board data found")
        return
    
    board_name = board_data.get("name", "Unknown")
    results = board_data.get("results", [])
    
    if not results:
        print(f"No results available for board '{board_name}'")
        return
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 60)
    
    if top:
        results = results[:top]
        print(f"Showing top {top} results:")
    
    for i, entry in enumerate(results, 1):
        name = entry.get("name", "Unknown")
        resolved = entry.get("resolved", "N/A")
        date = entry.get("date", "N/A")
        cost = entry.get("cost", "N/A")
        
        print(f"{i:2d}. {name}")
        print(f"     Resolved: {resolved}% | Date: {date} | Cost: ${cost}")
        
        # Show tags if available
        tags = entry.get("tags", [])
        if tags:
            print(f"     Tags: {', '.join(tags)}")
        
        print()


def main():
    parser = argparse.ArgumentParser(
        description="Fetch and display SWE bench leaderboard data",
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    
    parser.add_argument(
        '--board',
        type=str,
        required=True,
        help='Leaderboard type (e.g., Verified, Lite, Multimodal)'
    )
    
    parser.add_argument(
        '--top',
        type=int,
        help='Show top N results (optional)'
    )
    
    args = parser.parse_args()
    
    # Fetch data
    data = fetch_leaderboard_data()
    
    # Find the requested board
    board_data = find_leaderboard(data, args.board)
    
    if not board_data:
        available_boards = [board.get("name", "Unknown") for board in data.get("leaderboards", [])]
        print(f"Error: Board '{args.board}' not found. Available boards: {available_boards}", file=sys.stderr)
        sys.exit(1)
    
    # Display the requested board
    display_leaderboard(board_data, args.top)


if __name__ == "__main__":
    main()
