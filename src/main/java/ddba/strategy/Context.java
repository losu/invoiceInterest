package ddba.strategy;

import ddba.Invoice;
import ddba.Payment;

/**
 * Created by ddba on 07/04/2017.
 */
public class Context {
	private Invoice invoice;
	private Payment payment;

	private Strategy strategy;

	public Context() {
	}

	public Context(Invoice invoice, Payment payment) {
		this.invoice = invoice;
		this.payment = payment;
	}

	public Context(Strategy strategy) {
		this.strategy = strategy;
	}

//	public void executeStrategy(){
//		return strategy.execute();
//	}

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
