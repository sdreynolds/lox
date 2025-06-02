#include <gtest/gtest.h>


extern "C" {
#include "clox/src/debug.h"
}

TEST(OpCodeDissembly, ReturnOpCode) {
    testing::internal::CaptureStdout();

    Chunk chunk;
    initChunk(&chunk);
    writeChunk(&chunk, OP_RETURN);

    disassembleChunk(&chunk, "test chunk");
    freeChunk(&chunk);

    std::string output = testing::internal::GetCapturedStdout();
    EXPECT_EQ("== test chunk ==\n0000 OP_RETURN\n", output);
}
