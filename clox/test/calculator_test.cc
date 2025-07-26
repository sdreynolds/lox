#include <gtest/gtest.h>


extern "C" {
#include "clox/src/vm.h"
}

TEST(CalculatorTest, AddAndDivideAndNegate) {
    initVM();


    InterpretResult result = interpret("1.2 + 3.4 / 5.6 * -1;");

    freeVM();

    EXPECT_EQ(INTERPRET_OK, result);
}
