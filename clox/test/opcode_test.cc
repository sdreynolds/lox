#include <gtest/gtest.h>


extern "C" {
#include "clox/src/debug.h"
}

TEST(OpCodeDissembly, ReturnOpCode) {
    testing::internal::CaptureStdout();

    Chunk chunk;
    initChunk(&chunk);
    writeChunk(&chunk, OP_RETURN, 23);

    disassembleChunk(&chunk, "OP RETURN Test");
    freeChunk(&chunk);

    std::string output = testing::internal::GetCapturedStdout();
    EXPECT_EQ("== OP RETURN Test ==\n0000   23 OP_RETURN\n", output);
}

TEST(OpCodeDissembly, ConstantOpCode) {
    testing::internal::CaptureStdout();

    Chunk chunk;
    initChunk(&chunk);
    int constant = addConstant(&chunk, 1.2);
    writeChunk(&chunk, OP_CONSTANT, 98);
    writeChunk(&chunk, constant, 98);

    int secondConstant = addConstant(&chunk, 16.89);
    writeChunk(&chunk, OP_CONSTANT, 789);
    writeChunk(&chunk, secondConstant, 789);

    disassembleChunk(&chunk, "OP CONSTANT Test");
    freeChunk(&chunk);

    std::string output = testing::internal::GetCapturedStdout();
    EXPECT_EQ("== OP CONSTANT Test ==\n0000   98 OP_CONSTANT      0 '1.2'\n0002  789 OP_CONSTANT      1 '16.89'\n",
              output);
}
