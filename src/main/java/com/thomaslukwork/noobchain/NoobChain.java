package com.thomaslukwork.noobchain;

import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;

public class NoobChain {

  public static List<Block> blockChain = new ArrayList<>();
  public static int difficulty = 4;

  public static void main(String[] args) {

    blockChain.add(new Block("Hi im the first block", "0"));
    System.out.println("Trying to Mine block 1... ");
    blockChain.get(0).mineBlock(difficulty);

    blockChain.add(new Block("Yo im the second block",
        blockChain.get(blockChain.size() - 1).getHash()));
    System.out.println("Trying to Mine block 2... ");
    blockChain.get(1).mineBlock(difficulty);

    blockChain.add(new Block("Hey im the third block",
        blockChain.get(blockChain.size() - 1).getHash()));
    System.out.println("Trying to Mine block 3... ");
    blockChain.get(2).mineBlock(difficulty);

    System.out.println("\nBlockchain is Valid: " + isChainValid());

    String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
    System.out.println("\nThe block chain: ");
    System.out.println(blockChainJson);
  }


  public static boolean isChainValid() {
    Block currentBlock;
    Block previousBlock;
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');

    for (int i = 1; i < blockChain.size(); i++) {
      currentBlock = blockChain.get(i);
      previousBlock = blockChain.get(i - 1);

      if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
        System.out.println("Current Hashes not equal");
        return false;
      }

      if (!previousBlock.getHash().equals(previousBlock.calculateHash())) {
        System.out.println("Previous Hashes not equal");
        return false;
      }
      //check if hash is solved
      if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
        System.out.println("This block hasn't been mined");
        return false;
      }
    }
    return true;
  }

}
