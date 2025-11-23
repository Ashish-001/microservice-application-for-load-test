#!/bin/bash

# Script to build the microservices load test project
# Usage: ./scripts/build.sh [clean|compile|package]

BUILD_COMMAND=${1:-compile}

echo "Building microservices-load-test project..."
echo ""

cd "$(dirname "$0")/.." || exit

case $BUILD_COMMAND in
    clean)
        echo "Cleaning project..."
        mvn clean
        ;;
    compile)
        echo "Compiling project..."
        mvn clean compile
        ;;
    package)
        echo "Packaging project..."
        mvn clean package
        ;;
    *)
        echo "Invalid build command: $BUILD_COMMAND"
        echo "Usage: ./scripts/build.sh [clean|compile|package]"
        echo "  clean    - Clean the project (removes target directory)"
        echo "  compile  - Clean and compile the project (default)"
        echo "  package  - Clean, compile, and package the project (creates JAR)"
        exit 1
        ;;
esac

if [ $? -eq 0 ]; then
    echo ""
    echo "Build completed successfully!"
else
    echo ""
    echo "Build failed!"
    exit 1
fi

