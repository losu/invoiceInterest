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

public class StrategyForSettingNewValuesWhenFieldIsNull implements Strategy {

	private ArrayDeque<Invoice> invoiceCopy;
	private ArrayDeque<Payment> paymentsCopy;

	private ArrayDeque<Invoice> getInvoiceCopy() {
		return invoiceCopy;
	}

	private ArrayDeque<Payment> getPaymentsCopy() {
		return paymentsCopy;
	}

	public StrategyForSettingNewValuesWhenFieldIsNull(ArrayDeque<Invoice> invoiceCopy, ArrayDeque<Payment> paymentsCopy) {
		this.invoiceCopy = new ArrayDeque<>(invoiceCopy);

		this.paymentsCopy = new ArrayDeque<>(paymentsCopy);
	}

	@Override
	public boolean canExecute(Context context) {
		if (getInvoiceCopy().isEmpty() && getPaymentsCopy().isEmpty()) {
			return false;
		}

		return context.getInvoice() == null || context.getPayment() == null;
	}

	@Override
	public Tuple<Context, List<Output>> execute(Context context) {


		List<Output> outputs = new LinkedList<>();

		if (context.getInvoice() == null) {
			context.setInvoice(pollInvoice());
		}
		if (context.getPayment() == null) {
			context.setPayment(pollPayment());
		}

		return new Tuple<>(context, outputs);
	}

	private Invoice pollInvoice() {
		return invoiceCopy.pollFirst();
	}

	private Payment pollPayment() {
		return paymentsCopy.pollFirst();
	}
}
