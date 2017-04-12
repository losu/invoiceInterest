package ddba.strategy;

import ddba.Invoice;
import ddba.Payment;

public class Context {
	private Invoice invoice;
	private Payment payment;

	public Context() {
	}

	public Context(Invoice invoice, Payment payment) {
		this.invoice = invoice;
		this.payment = payment;
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
