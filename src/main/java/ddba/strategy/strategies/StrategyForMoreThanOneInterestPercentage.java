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

	/**
	 * Strategy destined for more than one interest percentage.
	 * <p>
	 * It checks whether there are more interest rate changes between date of payment and the deadline.
	 *
	 * @param context - contains information about payment and invoice
	 * @return if there is no additional interest rate changes then it returns false, else true
	 */
	@Override
	public boolean canExecute(Context context) {
		if (context.getInvoice() == null || context.getPayment() == null)
			return false;

		List<LocalDate> dates = datesOfChangedInterestRate(
				context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());
		if (!context.getInvoice().getInvoiceTitle().equals(context.getPayment().getPaymentTitle())) {
			return false;
		}

		return !dates.isEmpty();
	}

	/**
	 * Strategy destined for more than one interest percentage.
	 *
	 * @param context - contains information about payment and invoice
	 * @return pair of values, context and list of outputs
	 *
	 *
	 * @pre context cannot contain null values for either invoice or payment. Strategy is executed only
	 * if there are more interest rate between deadline and payment date.
	 * @post returns context with invoice reduced by payment and payment field set to null or with invoiced reduced
	 * by payment amount and payment set tu null.
	 */
	@Override
	public Tuple<Context, List<Output>> execute(Context context) {

		List<Output> outputs;

		outputs = generateOutputsForFakePayments(context);

		Output output = setupOutput(
				context.getInvoice(), context.getPayment());
		outputs.add(output);

		double diff = context.getInvoice().getAmount() - context.getPayment().getAmount();
		Context newContext;
		if (diff == 0.0) {
			newContext = new Context();
		} else {
			Invoice invoice = new Invoice(context.getInvoice().getInvoiceTitle(),context.getInvoice().getDeadlineDate(), diff);
			newContext = context;
			newContext.setInvoice(invoice);
			newContext.setPayment(null);
		}

		return new Tuple<>(newContext, outputs);
	}

	/**
	 * It creates fake payments with zero payment to generate proper outputs when interest percentage is being changed
	 * <p>
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
			Output output = setupOutput(context.getInvoice(), payment);
			Invoice invoiceTemp = new Invoice(date,context.getInvoice().getAmount());
			context.setInvoice(invoiceTemp);
			outputs.add(output);
		});


		//  from the new set payment date it is necessary to add 1 day for proper calculation
		LocalDate paymentDatePlusOneDay = context.getPayment().getPaymentDate();
		paymentDatePlusOneDay = paymentDatePlusOneDay.plusDays(1);
		Payment payment =  new Payment(paymentDatePlusOneDay,context.getPayment().getAmount());
		context.setPayment(payment);
		return outputs;
	}

	private Output setupOutput(Invoice invoice, Payment payment) {
		Output output = new Output();
		output.setDaysOverDeadline(DAYS.between(invoice.getDeadlineDate(), payment.getPaymentDate()));
		output.setPeriod(payment.getPaymentDate().minusDays(output.getDaysOverDeadline()) + " - " + payment.getPaymentDate());
		output.setInterestPercentage(decideInterestPercentage(invoice.getDeadlineDate()));
		output.setInterest(calculateInterest(invoice.getDeadlineDate(), invoice.getAmount(), payment.getPaymentDate()));

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

