#!/usr/bin/env python3
#  Copyright (c) 2020 the original author or authors
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied. See the License for the specific language governing
#  permissions and limitations under the License.

import argparse
import json
import sys
from urllib.request import urlopen


def fetch_leaderboard():
    """Fetch the SWE bench leaderboard data from GitHub."""
    url = "https://raw.githubusercontent.com/SWE-bench/swe-bench.github.io/master/data/leaderboards.json"
    try:
        with urlopen(url) as response:
            data = json.loads(response.read().decode())
        return data
    except Exception as e:
        print(f"Error fetching leaderboard: {e}", file=sys.stderr)
        sys.exit(1)


def display_leaderboard(data, board_name, top_n=None):
    """Display the leaderboard for the specified board."""
    boards = data.get("leaderboards", [])
    target_board = None
    
    for board in boards:
        if board.get("name") == board_name:
            target_board = board
            break
    
    if target_board is None:
        available_boards = [board.get("name") for board in boards]
        print(f"Error: Board '{board_name}' not found. Available boards: {', '.join(available_boards)}", file=sys.stderr)
        sys.exit(1)
    
    entries = target_board.get("results", [])
    
    if top_n is not None:
        entries = entries[:top_n]
    
    print(f"SWE Bench Leaderboard - {board_name}")
    print("=" * 50)
    
    for i, entry in enumerate(entries, 1):
        model = entry.get("name", "Unknown")
        resolved = entry.get("resolved", 0)
        print(f"{i:2d}. {model}: {resolved:.1f}%")


def main():
    parser = argparse.ArgumentParser(description="Fetch and display SWE bench leaderboard")
    parser.add_argument("--board", required=True, help="Leaderboard type (Verified, Lite, Multimodal, bash-only, Test)")
    parser.add_argument("--top", type=int, help="Show top N entries")
    
    args = parser.parse_args()
    
    data = fetch_leaderboard()
    display_leaderboard(data, args.board, args.top)


if __name__ == "__main__":
    main()
