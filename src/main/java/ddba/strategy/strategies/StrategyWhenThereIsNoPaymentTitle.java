package ddba.strategy.strategies;

import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.Tuple;
import ddba.strategy.Context;
import ddba.strategy.Strategy;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;

import static ddba.InterestRate.decideInterestPercentage;
import static ddba.strategy.InterestCalculation.datesOfChangedInterestRate;
import static java.time.temporal.ChronoUnit.DAYS;

public class StrategyWhenThereIsNoPaymentTitle implements Strategy {

	private ArrayDeque<Payment> paymentsCopy;

	private LocalDate now;

	private LocalDate getNow() {
		return now;
	}


	public StrategyWhenThereIsNoPaymentTitle(ArrayDeque<Payment> paymentsCopy, LocalDate now) {
		this.paymentsCopy = new ArrayDeque<>(paymentsCopy);
		this.now = now;
	}

	@Override
	public boolean canExecute(Context context) {
		if (context.getInvoice() == null || context.getPayment() == null) {
			return false;
		}
		return !context.getInvoice().getInvoiceTitle().equals(context.getPayment().getPaymentTitle());
	}

	@Override
	public Tuple<Context, List<Output>> execute(Context context) {
		
		List<Output> outputs = new LinkedList<>();
		Payment payment = findPaymentWithProperTitle(context.getInvoice().getInvoiceTitle());
		if (payment == null) {
			payment = new Payment(getNow(), 0.0);
		}
		context.setPayment(payment);

		List<LocalDate> dates = datesOfChangedInterestRate(
				context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());

		if (dates.isEmpty()) {
//			Output output = setupOutput(context.getInvoice(), context.getPayment(), context.getInvoice().getInvoice());
			Output output = new Output();
			output.setDaysOverDeadline(DAYS.between(context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate()));
			output.setPeriod(payment.getPaymentDate().minusDays(output.getDaysOverDeadline()) + " - " + payment.getPaymentDate());
			output.setInterestPercentage(decideInterestPercentage(context.getInvoice().getDeadlineDate()));
			output.setInterest(calculateInterest(context.getInvoice().getDeadlineDate(), context.getInvoice().getInvoice(), payment.getPaymentDate()));
			outputs.add(output);
		} else {
			outputs=generateOutputsForFakePayments(context);
		}
		context = new Context();

		return new Tuple<>(context, outputs);
	}

	private Payment findPaymentWithProperTitle(String title) {
		boolean flag = false;
		Payment payment = null;
		int counter =0;
		for (int i = 0; i < paymentsCopy.size(); ) {
			counter ++;
			Payment paymentTemp = paymentsCopy.pollFirst();
			if (title.equals(paymentTemp.getPaymentTitle())) {
				flag = true;
				payment = paymentTemp;
				counter --;
			} else {
				paymentsCopy.addLast(paymentTemp);
			}
			if(counter==paymentsCopy.size())
				break;
		}
		return flag ? payment : null;
	}

	private Output setupOutput(Invoice invoice, Payment payment, double invoiceTemp) {
		Output output = new Output();
		output.setDaysOverDeadline(DAYS.between(invoice.getDeadlineDate(), payment.getPaymentDate()));
		output.setPeriod(payment.getPaymentDate().minusDays(output.getDaysOverDeadline()) + " - " + payment.getPaymentDate());
		output.setInterestPercentage(decideInterestPercentage(invoice.getDeadlineDate()));
		output.setInterest(calculateInterest(invoice.getDeadlineDate(), invoiceTemp, payment.getPaymentDate()));

		return output;
	}

	/**
	 * it calculates how many days payment was over the deadline and then it calculate how much will interest
	 * be for these days
	 *
	 * @param deadlineDate - payment deadline
	 * @param invoice      - amount which has to be paid
	 * @param paymentDate  - actual date of payment
	 * @return - amount of interest which was comulated because of late payment
	 */
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
}

