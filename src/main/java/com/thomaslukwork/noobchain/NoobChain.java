package com.thomaslukwork.noobchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class NoobChain {

  public static List<Block> blockChain = new ArrayList<>();
  public static Map<String, TransactionOutput> UTXOs = new HashMap<>();

  public static int difficulty = 3;
  public static float minimumTransaction = 0.1f;
  public static Wallet walletA;
  public static Wallet walletB;
  public static Transaction genesisTransaction;

  public static void main(String[] args) {
    Security.addProvider(new BouncyCastleProvider());

    walletA = new Wallet();
    walletB = new Wallet();

    System.out.println("Private and public keys:");
    System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
    System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

    Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
    transaction.generateSignature(walletA.privateKey);

    System.out.println("Is signature verified");
    System.out.println(transaction.verifySignature());

  }

  /*
  // This is for testing block mining
  public static void main(String[] args) {

    blockChain.add(new Block("Hi im the first block", "0"));
    System.out.println("Trying to Mine block 1... ");
    System.out.println("Start block 1... " + new Date());
    blockChain.get(0).mineBlock(difficulty);
    System.out.println("Got block 1... " + new Date());

    blockChain.add(new Block("Yo im the second block",
        blockChain.get(blockChain.size() - 1).getHash()));
    System.out.println("Trying to Mine block 2... ");
    System.out.println("Start block 2... " + new Date());
    blockChain.get(1).mineBlock(difficulty);
    System.out.println("Got block 2... " + new Date());

    blockChain.add(new Block("Hey im the third block",
        blockChain.get(blockChain.size() - 1).getHash()));
    System.out.println("Trying to Mine block 3... ");
    System.out.println("Start block 3... " + new Date());
    blockChain.get(2).mineBlock(difficulty);
    System.out.println("Got block 3... " + new Date());

    System.out.println("\nBlockchain is Valid: " + isChainValid());

    String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
    System.out.println("\nThe block chain: ");
    System.out.println(blockChainJson);
  }
*/

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
