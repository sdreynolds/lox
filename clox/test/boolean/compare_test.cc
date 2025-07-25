#include <gtest/gtest.h>

extern "C" {
    #include "clox/src/vm.h"
    #include "clox/src/common.h"
}

#undef DEBUG_TRACE_EXECUTION
#undef DEBUG_PRINT_CODE

TEST(Boolean, GreaterThan) {
    testing::internal::CaptureStdout();
    initVM();
    interpret("print (1 + 3) * 2 > -14;");
    std::string output = testing::internal::GetCapturedStdout();


    // @TODO: would be nice if it didn't have all the debug info
    EXPECT_EQ("== <script> ==\n0000    1 OP_CONSTANT      0 '1'\n0002    | OP_CONSTANT      1 '3'\n0004    | OP_ADD\n0005    | OP_CONSTANT      2 '2'\n0007    | OP_MULTIPLY\n0008    | OP_CONSTANT      3 '14'\n0010    | OP_NEGATE\n0011    | OP_GREATER\n0012    | OP_PRINT\n0013    | OP_RETURN\n             [ <script> ]\n0000    1 OP_CONSTANT      0 '1'\n             [ <script> ][ 1 ]\n0002    | OP_CONSTANT      1 '3'\n             [ <script> ][ 1 ][ 3 ]\n0004    | OP_ADD\n             [ <script> ][ 4 ]\n0005    | OP_CONSTANT      2 '2'\n             [ <script> ][ 4 ][ 2 ]\n0007    | OP_MULTIPLY\n             [ <script> ][ 8 ]\n0008    | OP_CONSTANT      3 '14'\n             [ <script> ][ 8 ][ 14 ]\n0010    | OP_NEGATE\n             [ <script> ][ 8 ][ -14 ]\n0011    | OP_GREATER\n             [ <script> ][ true ]\n0012    | OP_PRINT\ntrue\n             [ <script> ]\n0013    | OP_RETURN\n", output);
}
