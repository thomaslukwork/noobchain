package com.thomaslukwork.noobchain;

import org.junit.Test;

public class BlockTest {

  @Test
  public void genBlock() {
    Block genesisBlock = new Block("0");
    System.out.println("previousHash for block 1 : " + genesisBlock.getPreviousHash());
    System.out.println("Hash for block 1 : " + genesisBlock.getHash());

    Block secondBlock = new Block(genesisBlock.getHash());
    System.out.println("previousHash for block 2 : " + secondBlock.getPreviousHash());
    System.out.println("Hash for block 2 : " + secondBlock.getHash());

    Block thirdBlock = new Block(secondBlock.getHash());
    System.out.println("previousHash for block 3 : " + thirdBlock.getPreviousHash());
    System.out.println("Hash for block 3 : " + thirdBlock.getHash());
  }


}
