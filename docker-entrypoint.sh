#!/bin/sh
set -e

if [ -n "$GRID_URL" ]; then
    echo "================================================================="
    echo "Waiting for Selenium Grid Hub & Browser Nodes at $GRID_URL/status to be UP..."
    echo "================================================================="
    count=0
    max_retries=30
    until curl -s "$GRID_URL/status" 2>/dev/null | grep -q '"UP"'; do
        count=$((count+1))
        if [ $count -gt $max_retries ]; then
            echo "ERROR: Selenium Grid Hub/Nodes failed to reach UP state after $max_retries attempts."
            exit 1
        fi
        echo "Selenium Grid nodes not registered yet (Attempt $count/$max_retries). Waiting 3 seconds..."
        sleep 3
    done
    echo "================================================================="
    echo "SUCCESS: Selenium Grid Hub & Browser Nodes are READY and UP!"
    echo "================================================================="
    sleep 3
fi

exec "$@"
