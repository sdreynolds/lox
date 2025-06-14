#include <gtest/gtest.h>


extern "C" {
#include "clox/src/scanner.h"
}

TEST(SimpleTokens, SkipWhiteSpace) {
    initScanner("        \n   \t    \t   \r\t\n");
    EXPECT_EQ(TOKEN_EOF, scanToken().type);
}

TEST(SimpleToken, SkipComments) {
    initScanner("/// a comment\n!=");
    Token firstToken = scanToken();
    Token secondToken = scanToken();

    EXPECT_EQ(TOKEN_BANG_EQUAL, firstToken.type);
    EXPECT_EQ(TOKEN_EOF, secondToken.type);
}

TEST(SimpleToken, MatchLookAhead) {
    initScanner(">=");
    EXPECT_EQ(TOKEN_GREATER_EQUAL, scanToken().type);
}

TEST(SimpleToken, NonMatch) {
    initScanner(">");
    EXPECT_EQ(TOKEN_GREATER, scanToken().type);
}
