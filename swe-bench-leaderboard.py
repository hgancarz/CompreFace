#!/usr/bin/env python3
"""
SWE Bench Leaderboard Fetcher

A utility script to fetch and display SWE bench leaderboard data.
"""

import argparse
import json
import sys
import urllib.request
from typing import Dict, Any, List, Optional


def fetch_leaderboard_data() -> Dict[str, Any]:
    """Fetch leaderboard data from the SWE bench GitHub repository."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    
    try:
        with urllib.request.urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard data: {e}", file=sys.stderr)
        sys.exit(1)


def get_board_data(data: Dict[str, Any], board_name: str) -> Optional[List[Dict[str, Any]]]:
    """Get data for a specific board by name."""
    for board in data.get("leaderboards", []):
        if board.get("name") == board_name:
            return board.get("results", [])
    return None


def display_leaderboard(data: Dict[str, Any], board: str, top: int = None) -> None:
    """Display the specified leaderboard."""
    board_data = get_board_data(data, board)
    
    if board_data is None:
        available_boards = [b["name"] for b in data.get("leaderboards", [])]
        print(f"Error: Board '{board}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    if not board_data:
        print(f"No data available for board '{board}'")
        return
    
    print(f"\nSWE Bench Leaderboard - {board}")
    print("=" * 60)
    
    # Display top entries if specified
    entries = board_data[:top] if top else board_data
    
    for i, entry in enumerate(entries, 1):
        name = entry.get("name", "Unknown")
        resolved = entry.get("resolved", "N/A")
        model_tags = [tag for tag in entry.get("tags", []) if tag.startswith("Model:")]
        model = model_tags[0].replace("Model: ", "") if model_tags else "N/A"
        date = entry.get("date", "N/A")
        
        print(f"{i}. {name}")
        print(f"   Score: {resolved}%")
        print(f"   Model: {model}")
        print(f"   Date: {date}")
        print()


def main():
    parser = argparse.ArgumentParser(description="Fetch SWE bench leaderboard data")
    parser.add_argument("--board", required=True, 
                       help="Leaderboard type to display")
    parser.add_argument("--top", type=int, 
                       help="Show only top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard_data()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
