package ddba.strategy.strategies;

import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.Tuple;
import ddba.strategy.Context;
import ddba.strategy.Strategy;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static ddba.InterestRate.decideInterestPercentage;
import static ddba.strategy.InterestCalculation.datesOfChangedInterestRate;
import static java.time.temporal.ChronoUnit.DAYS;

public class StrategyForEqualInvoiceAndPaymentAmount implements Strategy {

	/**
	 * Strategy destined for the case when payment and invoice are equal.
	 *
	 * @param context - contains information about payment and invoice
	 * @return true if payment amount and invoice amount are equal, otherwise it is false
	 */
	@Override
	public boolean canExecute(Context context) {
		if (context.getInvoice() == null || context.getPayment() == null) {
			return false;
		}

		List<LocalDate> dates = datesOfChangedInterestRate(context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());
		if (!context.getInvoice().getInvoiceTitle().equals(context.getPayment().getPaymentTitle())) {
			return false;
		}
		return dates.isEmpty() && context.getInvoice().getAmount() == context.getPayment().getAmount();
	}

	/**
	 * Strategy destined for the case when payment and invoice are equal.
	 *
	 * @param context - contains information about payment and invoice
	 * @return pair of values, context and list of outputs
	 *
	 *
	 * @pre context cannot contain null values for either invoice or payment. Strategy is executed only
	 * if both invoice are not null and the payment amount are equal.
	 * @post returns new context with fields invoice and payment equal to null.
	 */
	@Override
	public Tuple<Context, List<Output>> execute(Context context) {

		List<Output> outputs = new LinkedList<>();

		Output output = setupOutput(context.getInvoice(), context.getPayment(), context.getInvoice().getAmount());

		outputs.add(output);
		return new Tuple<>(new Context(), outputs);
	}

	private Output setupOutput(Invoice invoice, Payment payment, double invoiceTemp) {
		Output output = new Output();
		output.setDaysOverDeadline(DAYS.between(invoice.getDeadlineDate(), payment.getPaymentDate()));
		output.setPeriod(payment.getPaymentDate().minusDays(output.getDaysOverDeadline()) + " - " + payment.getPaymentDate());
		output.setInterestPercentage(decideInterestPercentage(invoice.getDeadlineDate()));
		output.setInterest(calculateInterest(invoice.getDeadlineDate(), invoiceTemp, payment.getPaymentDate()));

		return output;
	}

	private double calculateInterest(LocalDate deadlineDate, double invoice, LocalDate paymentDate) {

		double percentage = decideInterestPercentage(deadlineDate);
		double annualInterest = invoice * percentage / 100;

		double dailyInterest = annualInterest / 365;
		long numberOfDays = DAYS.between(deadlineDate, paymentDate);

		double interest = dailyInterest * numberOfDays;

		//rounding to decimal places
		interest = Math.round(interest * 100);
		interest = interest / 100;

		return interest;
	}
}
