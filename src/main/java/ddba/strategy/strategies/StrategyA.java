package ddba.strategy.strategies;

import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.Tuple;
import ddba.strategy.Context;
import ddba.strategy.Strategy;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ddba on 07/04/2017.
 */
public class StrategyA implements Strategy {

	private ArrayDeque<Invoice> invoiceCopy;
	private ArrayDeque<Payment> paymentsCopy;

	public ArrayDeque<Invoice> getInvoiceCopy() {
		return invoiceCopy;
	}

	public void setInvoiceCopy(ArrayDeque<Invoice> invoice) {
		this.invoiceCopy = new ArrayDeque<>(invoice);
	}

	public ArrayDeque<Payment> getPaymentsCopy() {
		return paymentsCopy;
	}

	public void setPaymentsCopy(ArrayDeque<Payment> payments) {
		this.paymentsCopy = new ArrayDeque<>(payments);
	}

	@Override
	public boolean canExecute(Context context) {
		if (getInvoiceCopy().isEmpty() && getPaymentsCopy().isEmpty()) {
			return false;
		}

		return context.getInvoice() == null || context.getPayment() == null;
	}

	@Override
	public Tuple<Context, LinkedList<Output>> execute(Context context) {
		if (!canExecute(context)) {
			return null;
		}

		Context newContext = context;

		if(context.getInvoice()==null){
			newContext.setInvoice(pollInvoice());
		}
		if(context.getPayment()==null){
			newContext.setPayment(pollPayment());
		}


		List<Output> outputs = new LinkedList<>();
		return new Tuple(newContext, outputs);
	}

	public Invoice pollInvoice() {
		return invoiceCopy.pollFirst();
	}
	public Payment pollPayment() {
		return paymentsCopy.pollFirst();
	}
}
