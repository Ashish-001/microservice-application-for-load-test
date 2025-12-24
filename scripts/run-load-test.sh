#!/bin/bash

# Load Test Runner Script
# Usage: ./scripts/run-load-test.sh [requests-per-service] [threads] [delay-ms] [gateway-url]

REQUESTS_PER_SERVICE=${1:-10000}
THREADS=${2:-20}
DELAY_MS=${3:-10}
GATEWAY_URL=${4:-http://35.198.90.238:9000}

echo "=========================================="
echo "  Running Load Test"
echo "=========================================="
echo "  Requests per service endpoint: $REQUESTS_PER_SERVICE"
echo "  Threads: $THREADS"
echo "  Delay: $DELAY_MS ms"
echo "  Gateway URL: $GATEWAY_URL"
echo "=========================================="
echo ""

cd "$(dirname "$0")/.." || exit 1

mvn exec:java -Dexec.mainClass="com.his.project.loadtest.LoadTestRunner" \
    -Dexec.args="--requests-per-service $REQUESTS_PER_SERVICE --threads $THREADS --delay $DELAY_MS --url $GATEWAY_URL"

