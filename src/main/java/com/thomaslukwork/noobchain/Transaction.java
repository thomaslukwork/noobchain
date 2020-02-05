package com.thomaslukwork.noobchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

  public String transactionId;
  public PublicKey sender;
  public PublicKey recipient;
  public float value;
  public byte[] signature;

  public List<TransactionInput> inputs = new ArrayList<>();
  public List<TransactionOutput> outputs = new ArrayList<>();

  public static int sequence = 0;

  public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
    this.sender = from;
    this.recipient = to;
    this.value = value;
    this.inputs = inputs;
  }

  // This Calculates the transaction hash (which will be used as its Id)
  private String calculateHash() {
    sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
    return StringUtil.applySha256(
        StringUtil.getStringFromKey(sender) +
            StringUtil.getStringFromKey(recipient) +
            Float.toString(value) + sequence
    );
  }

  //Signs all the data we don't wish to be tampered with.
  public void generateSignature(PrivateKey privateKey) {
    String data = StringUtil.getStringFromKey(sender)
        + StringUtil.getStringFromKey(recipient)
        + Float.toString(value);
    signature = StringUtil.applyECDSASig(privateKey, data);
  }

  //Verifies the data we signed hasn't been tampered with
  public boolean verifySignature() {
    String data = StringUtil.getStringFromKey(sender)
        + StringUtil.getStringFromKey(recipient)
        + Float.toString(value);
    return StringUtil.verifyECDSASig(sender, data, signature);
  }
}
