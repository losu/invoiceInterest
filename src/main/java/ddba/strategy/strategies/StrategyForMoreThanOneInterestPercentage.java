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


public class StrategyForMoreThanOneInterestPercentage implements Strategy {

	@Override
	public boolean canExecute(Context context) {
		if (context.getInvoice() == null || context.getPayment() == null)
			return false;

		List<LocalDate> dates = datesOfChangedInterestRate(
				context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());

		return !dates.isEmpty();
	}

	@Override
	public Tuple<Context, List<Output>> execute(Context context) {
		if (!canExecute(context)) {
			return null;
		}
		List<Output> outputs ;

		outputs = generateOutputsForFakePayments(context);

		Output output = setupOutput(
				context.getInvoice(), context.getPayment(), context.getInvoice().getInvoice());
		outputs.add(output);

		double diff = context.getInvoice().getInvoice() - context.getPayment().getPayment();
		Context newContext;
		if (diff == 0.0) {
			newContext = new Context();
		} else {
			newContext = context;
			newContext.getInvoice().setInvoice(diff);
			newContext.setPayment(null);
		}

		return new Tuple<>(newContext, outputs);
	}

	/**
	 * It creates fake payments with zero payment to generate proper outputs when interest percentage is being changed
	 *
	 * when actual payment was done late, then there is a risk that interest rate was changed compared to the one
	 * when the invoice payment deadline was set. So to calculate interest for those delay days method creates fake payment
	 * with zero amount for payment and set the date of payment to the one when interest rate has been changed.
	 *
	 * @param context - contains payment deadline and its actual payment date
	 * @return list of outputs with calculated amount of interest in each sector of interest rate
	 */
	private List<Output> generateOutputsForFakePayments(Context context) {
		List<LocalDate> dates = datesOfChangedInterestRate(
				context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());

		List<Output> outputs = new LinkedList<>();
		dates.forEach(date -> {
			Payment payment = new Payment(date.minusDays(1), 0.0);
			Output output = setupOutput(context.getInvoice(), payment, context.getInvoice().getInvoice());
			context.getInvoice().setDeadlineDate(date);
			outputs.add(output);
		});
		return outputs;
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

