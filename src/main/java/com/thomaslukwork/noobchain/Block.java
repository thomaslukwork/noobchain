package com.thomaslukwork.noobchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {

  private String hash;
  private String previousHash;
  public String merkleRoot;
  public List<Transaction> transactions = new ArrayList<>(); //our data will be a simple message.
  private long timestamp;
  private int nonce;

  public Block(String previousHash) {
    this.previousHash = previousHash;
    this.timestamp = new Date().getTime();
    this.hash = calculateHash();
  }

  public String calculateHash() {
    return StringUtil.applySha256(previousHash
        + Long.toString(timestamp)
        + Integer.toString(nonce)
        + merkleRoot);
  }

  //Increases nonce value until hash target is reached.
  public void mineBlock(int difficulty) {
    String target = new String(new char[difficulty])
        .replace('\0', '0'); //Create a string with difficulty * "0"
    while (!hash.substring(0, difficulty).equals(target)) {
      nonce++;
      hash = calculateHash();
    }
    System.out.println("Block Mined!!! : " + hash);
  }

  //Add transactions to this block
  public boolean addTransaction(Transaction transaction) {
    //process transaction and check if valid, unless block is genesis block then ignore.
    if (transaction == null) {
      return false;
    }
    if (!previousHash.equals("0")) {
      if (!transaction.processTransaction()) {
        System.out.println("Transaction failed to process. Discarded.");
        return false;
      }
    }
    transactions.add(transaction);
    System.out.println("Transaction Successfully added to Block");
    return true;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getPreviousHash() {
    return previousHash;
  }

  public void setPreviousHash(String previousHash) {
    this.previousHash = previousHash;
  }

  public String getMerkleRoot() {
    return merkleRoot;
  }

  public void setMerkleRoot(String merkleRoot) {
    this.merkleRoot = merkleRoot;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public int getNonce() {
    return nonce;
  }

  public void setNonce(int nonce) {
    this.nonce = nonce;
  }
}
