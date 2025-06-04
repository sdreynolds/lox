#include <stdlib.h>
#include "chunk.h"
#include "memory.h"
#include <stdio.h>

void initChunk(Chunk* chunk) {
    chunk->count = 0;
    chunk->capacity = 0;
    chunk->uniqueLines = 0;
    chunk->uniqueLineCapacity = 0;
    chunk->code = NULL;
    chunk->lines = NULL;
    initValueArray(&chunk->constants);
}

void writeChunk(Chunk* chunk, uint8_t byte, int line) {
    if (chunk->capacity < chunk->count + 1) {
        int oldCapacity = chunk->capacity;
        chunk->capacity = GROW_CAPACITY(oldCapacity);
        chunk->code = GROW_ARRAY(uint8_t, chunk->code, oldCapacity, chunk->capacity);
    }

    bool newLine = false;
    if (chunk->count == 0 || getLine(chunk, chunk->count - 1 ) != line) {
        newLine = true;
    }

    if (newLine && chunk->uniqueLineCapacity < chunk->uniqueLines + 1) {
        int oldCapacity = chunk->uniqueLineCapacity;
        chunk->uniqueLineCapacity = GROW_CAPACITY(oldCapacity);
        chunk->lines = GROW_ARRAY(LineRunLength, chunk->lines, oldCapacity, chunk->uniqueLineCapacity);
    }

    if (newLine) {
        LineRunLength nextLine;
        initLineRunLength(&nextLine, line);
        chunk->lines[chunk->uniqueLines] = nextLine;
        chunk->uniqueLines++;
    } else {
        chunk->lines[chunk->uniqueLines - 1].repeatedValues++;
    }

    chunk->code[chunk->count] = byte;
    chunk->count++;
}

void initLineRunLength(LineRunLength* runLength, int line) {
    runLength->line = line;
    runLength->repeatedValues = 1;
}

int getLine(Chunk* chunk, int offset) {
    int calculatedOffset = -1;
    int i = 0;
    for (; i < chunk->uniqueLines; i++) {
        calculatedOffset += chunk->lines[i].repeatedValues;
        if (calculatedOffset >= offset) {
            break;
        }
    }
    return chunk->lines[i].line;
}

void freeChunk(Chunk* chunk) {
    FREE_ARRAY(uint8_t, chunk->code, chunk->capacity);
    FREE_ARRAY(LineRunLength, chunk->lines, chunk->capacity);
    freeValueArray(&chunk->constants);
    initChunk(chunk);
}

int addConstant(Chunk* chunk, Value value) {
    writeValueArray(&chunk->constants, value);
    return chunk->constants.count - 1;
}
