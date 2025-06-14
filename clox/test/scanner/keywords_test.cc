#include <gtest/gtest.h>


extern "C" {
#include "clox/src/scanner.h"
}

TEST(Keyword, AND) {
    initScanner("and");
    EXPECT_EQ(TOKEN_AND, scanToken().type);
}
TEST(Keyword, CLASS) {
    initScanner("class");
    EXPECT_EQ(TOKEN_CLASS, scanToken().type);
}
TEST(Keyword, ELSE) {
    initScanner("else");
    EXPECT_EQ(TOKEN_ELSE, scanToken().type);
}
TEST(Keyword, FALSE) {
    initScanner("false");
    EXPECT_EQ(TOKEN_FALSE, scanToken().type);
}
TEST(Keyword, FOR) {
    initScanner("for");
    EXPECT_EQ(TOKEN_FOR, scanToken().type);
}
TEST(Keyword, FUN) {
    initScanner("fun");
    EXPECT_EQ(TOKEN_FUN, scanToken().type);
}
TEST(Keyword, IF){
    initScanner("if");
    EXPECT_EQ(TOKEN_IF, scanToken().type);
}
TEST(Keyword, NIL) {
    initScanner("nil");
    EXPECT_EQ(TOKEN_NIL, scanToken().type);
}
TEST(Keyword, OR) {
    initScanner("or");
    EXPECT_EQ(TOKEN_OR, scanToken().type);
}
TEST(Keyword, PRINT) {
    initScanner("print");
    EXPECT_EQ(TOKEN_PRINT, scanToken().type);
}
TEST(Keyword, RETURN) {
    initScanner("return");
    EXPECT_EQ(TOKEN_RETURN, scanToken().type);
}
TEST(Keyword, SUPER) {
    initScanner("super");
    EXPECT_EQ(TOKEN_SUPER, scanToken().type);
}
TEST(Keyword, THIS) {
    initScanner("this");
    EXPECT_EQ(TOKEN_THIS, scanToken().type);
}
TEST(Keyword, TRUE) {
    initScanner("true");
    EXPECT_EQ(TOKEN_TRUE, scanToken().type);
}
TEST(Keyword, VAR) {
    initScanner("var");
    EXPECT_EQ(TOKEN_VAR, scanToken().type);
}
TEST(Keyword, WHILE) {
    initScanner("while");
    EXPECT_EQ(TOKEN_WHILE, scanToken().type);
}
TEST(Identifier, Forest) {
    initScanner("forest");
    EXPECT_EQ(TOKEN_IDENTIFIER, scanToken().type);
}
