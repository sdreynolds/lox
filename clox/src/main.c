#include "chunk.h"
#include "debug.h"

int main(int argc, const char* argv[]) {
    Chunk chunk;
    initChunk(&chunk);
    int constant = addConstant(&chunk, 1.2);
    writeChunk(&chunk, OP_CONSTANT, 98);
    writeChunk(&chunk, constant, 98);

    int secondConstant = addConstant(&chunk, 16.89);
    writeChunk(&chunk, OP_CONSTANT, 789);
    writeChunk(&chunk, secondConstant, 789);

    writeChunk(&chunk, OP_CONSTANT, 789);
    writeChunk(&chunk, addConstant(&chunk, 1689.89), 789);

    disassembleChunk(&chunk, "test chunk");
    freeChunk(&chunk);
    return 0;
}
