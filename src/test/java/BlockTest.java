import org.junit.Test;

public class BlockTest {

  @Test
  public void genBlock() {
    Block genesisBlock = new Block("Hi im the first block", "0");
    System.out.println("previousHash for block 1 : " + genesisBlock.getPreviousHash());
    System.out.println("Hash for block 1 : " + genesisBlock.getHash());

    Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash());
    System.out.println("previousHash for block 2 : " + secondBlock.getPreviousHash());
    System.out.println("Hash for block 2 : " + secondBlock.getHash());

    Block thirdBlock = new Block("Hey im the third block", secondBlock.getHash());
    System.out.println("previousHash for block 3 : " + thirdBlock.getPreviousHash());
    System.out.println("Hash for block 3 : " + thirdBlock.getHash());
  }


}
