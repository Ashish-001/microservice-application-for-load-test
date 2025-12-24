#!/bin/bash

# Script to load demo data into microservices
# Usage: ./scripts/load-demo-data.sh [API_GATEWAY_URL]

API_GATEWAY_URL=${1:-http://35.198.90.238:9000}

echo "Loading demo data to: $API_GATEWAY_URL"
echo ""

cd "$(dirname "$0")/.." || exit

# Compile if needed
if [ ! -d "target/classes" ]; then
    echo "Compiling project..."
    mvn clean compile -q
fi

# Run the demo data loader
# Explicitly specify mainClass to ensure we run DemoDataLoader, not LoadTestRunner
mvn exec:java \
    -Dexec.mainClass="com.his.project.loadtest.DemoDataLoader" \
    -Dexec.args="$API_GATEWAY_URL" \
    -q

