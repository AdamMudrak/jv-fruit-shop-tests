package core.basesyntax.services.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.db.Storage;
import core.basesyntax.exceptions.NegativeResultException;
import core.basesyntax.exceptions.NegativeValueForOperationException;
import core.basesyntax.models.FruitTransaction;
import core.basesyntax.services.handlers.impl.PurchaseOperationHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PurchaseOperationHandlerTest {
    public static final int EXPECTED_RESULT_QUANTITY_OF_ORANGE = 90;
    public static final int EXPECTED_RESULT_QUANTITY_OF_APPLE = 90;
    public static final int EXPECTED_RESULT_QUANTITY_OF_BANANA = 90;
    private static final FruitTransaction PURCHASE_WITH_NEGATIVE_RESULT =
            new FruitTransaction(FruitTransaction.Operation.PURCHASE, Constants.BANANA, 1000);
    private static final String EXPECTED_EXCEPTION_MESSAGE_NEGATIVE_NUMBER_PURCHASE =
            "Purchase operation value for banana should've been positive but was " +
                    Constants.BANANA_FRUITTRANSACTION_NEGATIVE_QUANTITY.getQuantity();
    private static final String EXPECTED_EXCEPTION_MESSAGE_BUY_MORE_THAN_HAVE =
            "Insufficient stock for purchase: Requested " +
                    PURCHASE_WITH_NEGATIVE_RESULT.getQuantity() + " but only " +
                    Constants.INITIAL_QUANTITY_OF_BANANA + " available for " +
                    PURCHASE_WITH_NEGATIVE_RESULT.getFruit();
    private static OperationHandler purchaseOperationHandler;

    @BeforeAll
    static void initPurchaseOperationHandlerAndStorage() {
        purchaseOperationHandler = new PurchaseOperationHandler();
        Storage.updateFruit(Constants.ORANGE, Constants.INITIAL_QUANTITY_OF_ORANGE);
        Storage.updateFruit(Constants.APPLE, Constants.INITIAL_QUANTITY_OF_APPLE);
        Storage.updateFruit(Constants.BANANA, Constants.INITIAL_QUANTITY_OF_BANANA);
    }

    @AfterAll
    static void closePurchaseOperationHandler() {
        purchaseOperationHandler = null;
    }

    @Test
    void handleOperation_purchaseNegativeQuantity_notOk() {
        RuntimeException negativeValueForOperationException =
                assertThrows(NegativeValueForOperationException.class,
                        () -> purchaseOperationHandler.handleOperation(
                                Constants.BANANA_FRUITTRANSACTION_NEGATIVE_QUANTITY));
        assertEquals(negativeValueForOperationException.getMessage(),
                EXPECTED_EXCEPTION_MESSAGE_NEGATIVE_NUMBER_PURCHASE);
    }

    @Test
    void handleOperation_purchaseMoreThanHave_notOk() {
        RuntimeException negativeResultException = assertThrows(NegativeResultException.class,
                () -> purchaseOperationHandler.handleOperation(PURCHASE_WITH_NEGATIVE_RESULT));
        assertEquals(negativeResultException.getMessage(),
                EXPECTED_EXCEPTION_MESSAGE_BUY_MORE_THAN_HAVE);
    }

    @Test
    void handleOperation_goodTransactions_Ok() {
        purchaseOperationHandler.handleOperation(Constants.ORANGE_FRUITTRANSACTION);
        purchaseOperationHandler.handleOperation(Constants.APPLE_FRUITTRANSACTION);
        purchaseOperationHandler.handleOperation(Constants.BANANA_FRUITTRANSACTION);
        assertEquals(EXPECTED_RESULT_QUANTITY_OF_ORANGE, Storage.getFruits().get(Constants.ORANGE));
        assertEquals(EXPECTED_RESULT_QUANTITY_OF_APPLE, Storage.getFruits().get(Constants.APPLE));
        assertEquals(EXPECTED_RESULT_QUANTITY_OF_BANANA, Storage.getFruits().get(Constants.BANANA));
    }
}
