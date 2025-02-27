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

  public static int difficulty = 5;
  public static float minimumTransaction = 0.1f;
  public static Wallet walletA;
  public static Wallet walletB;
  public static Transaction genesisTransaction;

  public static void main(String[] args) {
    //Setup Bouncey castle as a Security Provider
    Security.addProvider(new BouncyCastleProvider());
    //Create the new wallets
    walletA = new Wallet();
    walletB = new Wallet();
    Wallet coinbase = new Wallet();

    //create genesis transaction, which sends 100 NoobCoin to walletA:
    genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
    genesisTransaction
        .generateSignature(coinbase.privateKey);   //manually sign the genesis transaction
    genesisTransaction.transactionId = "0"; //manually set the transaction id
    genesisTransaction.outputs.add(
        new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value,
            genesisTransaction.transactionId)); //manually add the Transactions Output
    UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs
        .get(0)); //its important to store our first transaction in the UTXOs list.

    System.out.println("Creating and Mining Genesis block... ");
    Block genesis = new Block("0");
    genesis.addTransaction(genesisTransaction);
    addBlock(genesis);

    //testing
    Block block1 = new Block(genesis.getHash());
    System.out.println("\nWalletA's balance is: " + walletA.getBalance());
    System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
    block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
    addBlock(block1);
    System.out.println("\nWalletA's balance is: " + walletA.getBalance());
    System.out.println("WalletB's balance is: " + walletB.getBalance());

    Block block2 = new Block(block1.getHash());
    System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
    block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
    addBlock(block2);
    System.out.println("\nWalletA's balance is: " + walletA.getBalance());
    System.out.println("WalletB's balance is: " + walletB.getBalance());

    Block block3 = new Block(block2.getHash());
    System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
    block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
    System.out.println("\nWalletA's balance is: " + walletA.getBalance());
    System.out.println("WalletB's balance is: " + walletB.getBalance());

    isChainValid();
/*

    //Test public and private keys
    System.out.println("Private and public keys:");
    System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
    System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
    //Create a test transaction from WalletA to walletB
    Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
    transaction.generateSignature(walletA.privateKey);
    //Verify the signature works and verify it from the public key
    System.out.println("Is signature verified");
    System.out.println(transaction.verifySignature());
*/

  }

  public static Boolean isChainValid() {
    Block currentBlock;
    Block previousBlock;
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');
    HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
    tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

    //loop through blockchain to check hashes:
    for (int i = 1; i < blockChain.size(); i++) {

      currentBlock = blockChain.get(i);
      previousBlock = blockChain.get(i - 1);
      //compare registered hash and calculated hash:
      if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
        System.out.println("#Current Hashes not equal");
        return false;
      }
      //compare previous hash and registered previous hash
      if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
        System.out.println("#Previous Hashes not equal");
        return false;
      }
      //check if hash is solved
      if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
        System.out.println("#This block hasn't been mined");
        return false;
      }

      //loop thru blockchains transactions:
      TransactionOutput tempOutput;
      for (int t = 0; t < currentBlock.transactions.size(); t++) {
        Transaction currentTransaction = currentBlock.transactions.get(t);

        if (!currentTransaction.verifySignature()) {
          System.out.println("#Signature on Transaction(" + t + ") is Invalid");
          return false;
        }
        if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
          System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
          return false;
        }

        for (TransactionInput input : currentTransaction.inputs) {
          tempOutput = tempUTXOs.get(input.transactionOutputId);

          if (tempOutput == null) {
            System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
            return false;
          }

          if (input.UTXO.value != tempOutput.value) {
            System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
            return false;
          }

          tempUTXOs.remove(input.transactionOutputId);
        }

        for (TransactionOutput output : currentTransaction.outputs) {
          tempUTXOs.put(output.id, output);
        }

        if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
          System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
          return false;
        }
        if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
          System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
          return false;
        }

      }

    }
    System.out.println("Blockchain is valid");
    return true;
  }

  public static void addBlock(Block newBlock) {
    newBlock.mineBlock(difficulty);
    blockChain.add(newBlock);
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

  /*
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
*/

}
