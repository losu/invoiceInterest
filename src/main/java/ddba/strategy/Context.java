package ddba.strategy;

import com.sun.istack.internal.Nullable;
import ddba.Invoice;
import ddba.Payment;

/**
 * Context contains two fields invoice and payment. Object that is being consumed by strategies
 *
 * @pre before consumption fields: invoice and payment are expected to be null
 * @post each of the fields are dependent on the specific strategy.
 */
public class Context {

	@Nullable
	private Invoice invoice;
	@Nullable
	private Payment payment;

	public Context() {
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
}
