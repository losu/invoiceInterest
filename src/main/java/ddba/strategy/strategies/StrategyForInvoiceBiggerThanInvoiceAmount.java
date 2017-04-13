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

public class StrategyForInvoiceBiggerThanInvoiceAmount implements Strategy {

	@Override
	public boolean canExecute(Context context) {
		if (context.getInvoice() == null || context.getPayment() == null)
			return false;

		List<LocalDate> dates = datesOfChangedInterestRate(context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());
		if (!dates.isEmpty())
			return false;

		if (context.getPayment().getPayment() <= 0.0)
			return false;

		if(!context.getInvoice().getInvoiceTitle().equals(context.getPayment().getPaymentTitle()))
			return false;

		return context.getInvoice().getInvoice() > context.getPayment().getPayment();
	}

	@Override
	public Tuple<Context, List<Output>> execute(Context context) {
		List<Output> outputs = new LinkedList<>();

		double diff = context.getInvoice().getInvoice() - context.getPayment().getPayment();

		Output output = setupOutput(context.getInvoice(), context.getPayment(), context.getInvoice().getInvoice());

		outputs.add(output);
		context.getInvoice().setInvoice(diff);
		context.setPayment(null);

		return new Tuple<>(context, outputs);
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

