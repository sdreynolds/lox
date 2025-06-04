#ifndef clox_chunk_h
#define clox_chunk_h

#include "common.h"
#include "value.h"

typedef enum {
    OP_CONSTANT,
    OP_RETURN,
} OpCode;


typedef struct {
    int line;
    int repeatedValues;
} LineRunLength;

typedef struct {
    int count;
    int capacity;
    uint8_t* code;
    int uniqueLines;
    int uniqueLineCapacity;
    LineRunLength* lines;
    ValueArray constants;
} Chunk;

void initChunk(Chunk* chunk);
void freeChunk(Chunk* chunk);
int addConstant(Chunk* chunk, Value value);
void writeChunk(Chunk* chunk, uint8_t byte, int line);
int getLine(Chunk* chunk, int offset);


void initLineRunLength(LineRunLength* runLength, int line);
#endif
