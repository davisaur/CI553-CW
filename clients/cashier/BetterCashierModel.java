package clients.cashier;

import catalogue.Basket;
import catalogue.BetterBasket;
import middle.MiddleFactory;

public class BetterCashierModel extends CashierModel {
    /**
     * Construct the model of the Cashier
     *
     * @param mf The factory to create the connection objects
     */
    public BetterCashierModel(MiddleFactory mf) {
        super(mf);
    }

    @Override
    protected Basket makeBasket() {
        return new BetterBasket();
    }
}
