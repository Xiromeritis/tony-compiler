#!/bin/bash

# Check if at least one file was provided
if [ "$#" -eq 0 ]; then
    echo "Usage: ./run_all.sh <file1.tony> [file2.tony ...]"
    exit 1
fi

echo "Preparing library (lib.c -> lib.ll)..."
clang -S -emit-llvm lib.c -o lib.ll
if [ $? -ne 0 ]; then
    echo "Error: Failed to compile lib.c"
    exit 1
fi

# Process each file provided as an argument
for file in "$@"; do
    if [ ! -f "$file" ]; then
        echo "Error: File $file not found. Skipping."
        continue
    fi

    basename=$(basename "$file" .tony)
    echo "----------------------------------------"
    echo "Processing: $file"
    echo "----------------------------------------"

    # 1. Generate LLVM IR (output.ll)
    java -jar target/compiler-0.5.jar "$file" > /dev/null
    if [ $? -ne 0 ]; then
        echo "Error during compilation of $file."
        continue
    fi

    # Rename output.ll to avoid conflicts with other files
    mv output.ll "${basename}_out.ll"

    # 2. Link the generated code with lib.ll
    llvm-link "${basename}_out.ll" lib.ll -S -o "${basename}_combined.ll"
    if [ $? -ne 0 ]; then
        echo "Error during linking of $file."
        continue
    fi

    # 3. Execute using lli
    echo "[Execution Result - $basename]"
    lli "${basename}_combined.ll"

    echo ""
done

echo "----------------------------------------"
echo "Process completed."
