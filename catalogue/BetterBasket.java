package catalogue;

import java.io.Serializable;
import java.util.Collections;

/**
 * Write a description of class BetterBasket here.
 * 
 * @author  Your Name 
 * @version 1.0
 */
public class BetterBasket extends Basket implements Serializable
{
  private static final long serialVersionUID = 1L;

  // You need to add code here
  @Override
  public boolean add(Product pr) {
    for (Product product : this) {
      if (product.getProductNum().equals(pr.getProductNum())) {
        int newQuantity = product.getQuantity() + 1;
        product.setQuantity(newQuantity);
        return true;
      }
    }
    return super.add(pr);
  }
}

