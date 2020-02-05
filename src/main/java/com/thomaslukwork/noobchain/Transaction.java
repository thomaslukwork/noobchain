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

  public boolean processTransaction() {

    if (!verifySignature()) {
      System.out.println("#Transaction Signature failed to verify");
      return false;
    }

    //gather transaction inputs (Make sure they are unspent):
    for (TransactionInput transactionInput : inputs) {
      transactionInput.UTXO = NoobChain.UTXOs.get(transactionInput.transactionOutputId);
    }

    if (getInputsValue() < NoobChain.minimumTransaction) {
      System.out.println("#Transaction Inputs to small: " + getInputsValue());
      return false;
    }

    //generate transaction outputs:
    float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
    transactionId = calculateHash();

    outputs.add(new TransactionOutput(this.recipient, value, transactionId));
    outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

    //add outputs to Unspent list
    for (TransactionOutput transactionOutput : outputs) {
      NoobChain.UTXOs.put(transactionOutput.id, transactionOutput);
    }

    //remove transaction inputs from UTXO lists as spent:
    for (TransactionInput transactionInput : inputs) {
      if (transactionInput.UTXO == null) {
        continue; //if Transaction can't be found skip it
      }
      NoobChain.UTXOs.remove(transactionInput.UTXO.id);
    }
    return true;
  }

  //returns sum of inputs(UTXOs) values
  public float getInputsValue() {
    float total = 0;
    for (TransactionInput transactionInput : inputs) {
      if (transactionInput.UTXO == null) {
        continue; //if Transaction can't be found skip it
      }
      total += transactionInput.UTXO.value;
    }
    return total;
  }

  //returns sum of outputs:
  public float getOutputsValue() {
    float total = 0;
    for (TransactionOutput transactionOutput : outputs) {
      total += transactionOutput.value;
    }
    return total;
  }
}
