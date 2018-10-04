#!/usr/bin/env bash

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${ROOT}"
source venv/bin/activate
screen -dmS rulesbot venv/bin/python main.py
