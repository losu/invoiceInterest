package ddba.strategy.strategies;

import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.Tuple;
import ddba.strategy.Context;
import ddba.strategy.Strategy;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;

public final class StrategyForSettingNewValuesWhenFieldIsNull implements Strategy {

	private final ArrayDeque<Invoice> invoiceCopy;
	private final ArrayDeque<Payment> paymentsCopy;

	public StrategyForSettingNewValuesWhenFieldIsNull(ArrayDeque<Invoice> initialInvoices, ArrayDeque<Payment> initialPayments) {
		this.invoiceCopy = new ArrayDeque<>(initialInvoices);
		this.paymentsCopy = new ArrayDeque<>(initialPayments);
	}

	/**
	 * Strategy destined for initializing values which are null
	 * <p>
	 * It checks whether context contain null payment or invoice and it set with value from the input queue of
	 * invoices or payments
	 *
	 * @param context - contains information about payment and invoice
	 * @return true if context contains either payment field of invoice field equal to null, otherwise returns false
	 */
	@Override
	public boolean canExecute(Context context) {
		if (invoiceCopy.isEmpty() && paymentsCopy.isEmpty()) {
			return false;
		}

		return context.getInvoice() == null || context.getPayment() == null;
	}

	/**
	 * Strategy destined for initializing values which are null
	 *
	 * @param context - contains information about payment and invoice
	 * @return pair of values, context and list of outputs
	 *
	 * @pre context should contain null values for either invoice or payment. Strategy is executed only
	 * if both or one of the context field are null.
	 * @post returns context with invoice and payment fields set with value from input queue of
	 * invoices or payments.
	 */
	@Override
	public Tuple<Context, List<Output>> execute(Context context) {

		if (context.getInvoice() == null) {
			context.setInvoice(invoiceCopy.pollFirst());
		}
		if (context.getPayment() == null) {
			context.setPayment(paymentsCopy.pollFirst());
		}

		return new Tuple<>(context, Collections.emptyList());
	}
}
