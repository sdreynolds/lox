#include <gtest/gtest.h>


extern "C" {
#include "clox/src/vm.h"
}

TEST(CalculatorTest, AddAndDivideAndNegate) {
    initVM();

    Chunk chunk;
    initChunk(&chunk);
    int constant = addConstant(&chunk, NUMBER_VAL(1.2));
    writeChunk(&chunk, OP_CONSTANT, 789);
    writeChunk(&chunk, constant, 789);

    constant = addConstant(&chunk, NUMBER_VAL(3.4));
    writeChunk(&chunk, OP_CONSTANT, 789);
    writeChunk(&chunk, constant, 789);

    writeChunk(&chunk, OP_ADD, 789);

    constant = addConstant(&chunk, NUMBER_VAL(5.6));
    writeChunk(&chunk, OP_CONSTANT, 789);
    writeChunk(&chunk, constant, 789);

    writeChunk(&chunk, OP_DIVIDE, 789);
    writeChunk(&chunk, OP_NEGATE, 790);

    writeChunk(&chunk, OP_RETURN, 800);

    InterpretResult result = interpretDirect(&chunk);

    freeChunk(&chunk);
    freeVM();

    EXPECT_EQ(INTERPRET_OK, result);
}
