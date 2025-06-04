#include "chunk.h"
#include "debug.h"
#include "vm.h"

int main(int argc, const char* argv[]) {
    initVM();

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

    writeChunk(&chunk, OP_RETURN, 800);

    interpret(&chunk);

    freeChunk(&chunk);
    freeVM();
    return 0;
}
