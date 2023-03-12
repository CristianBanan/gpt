package se.extenda.pos.countrymodule.germany.transaction;

import se.extenda.core.component.AbstractComponent;
import se.extenda.core.component.annotation.Component;
import se.extenda.core.component.annotation.InstanceRef;
import se.extenda.pos.base.client.transaction.TransactionManager;
import se.extenda.pos.base.client.transaction.TransactionManagerListener;
import se.extenda.pos.base.common.transaction.ControlTransaction;
import se.extenda.pos.base.common.transaction.ElementUtilities;
import se.extenda.pos.base.common.transaction.RetailTransaction;
import se.extenda.pos.base.common.transaction.element.ApiAttributeElement;
import se.extenda.pos.base.common.transaction.element.Element;
import se.extenda.util.ApplicationException;

/**
 * This class add it self as a listener on {@link TransactionManager} to be able to capture all
 * transactions that has an active retail transaction id.<br>
 * For Germany we have to set a Fiskaly active transaction id on the opening transaction request in
 * order to use it as a supplier when we finish the transaction.<br>
 * We need to remove active transaction id when transaction isn't active anymore.
 */
@InstanceRef(
    name = "TransactionManager",
    type = se.extenda.pos.base.client.transaction.TransactionManager.class)
@Component(name = "FiscalActiveTransactionListener")
public class FiscalActiveTransactionListener extends AbstractComponent
    implements TransactionManagerListener {
  private static final String ACTIVE_TRANSACTION_ID = "ffw_ActiveTransactionId";

  @Override
  public void create() throws ApplicationException {
    TransactionManager transactionManager = getInstance("TransactionManager");
    transactionManager.registerTransactionManagerListener(this);
  }

  @Override
  public void beforeStore(RetailTransaction transaction) throws ApplicationException {
    debug("beforeStore( " + transaction.getTransactionId() + "  )");
    removeActiveTransactionId(transaction.getRootElement());
  }

  @Override
  public void beforeStore(ControlTransaction transaction) throws ApplicationException {
    debug("beforeStore( " + transaction.getTransactionId() + "  )");
    removeActiveTransactionId(transaction.getRootElement());
  }

  @Override
  public void beforePark(RetailTransaction transaction) throws ApplicationException {
    debug("beforePark( " + transaction.getTransactionId() + "  )");
    removeActiveTransactionId(transaction.getRootElement());
  }

  @Override
  public void beforeCancel(RetailTransaction transaction) throws ApplicationException {
    debug("beforeCancel( " + transaction.getTransactionId() + "  )");
    removeActiveTransactionId(transaction.getRootElement());
  }

  /**
   * Remove active transaction id from transaction if present.
   *
   * @param rootElement
   */
  private void removeActiveTransactionId(Element rootElement) {
    ApiAttributeElement apiAttributeElement =
        ElementUtilities.firstInclInvalidated(
            rootElement,
            ApiAttributeElement.class,
            it -> ACTIVE_TRANSACTION_ID.equals(it.getName()));
    if (apiAttributeElement != null) {
      apiAttributeElement.remove();
    }
  }
}
