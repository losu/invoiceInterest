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

public class StrategyWhenInvoiceTitleAndPaymentTitleAreNotEqual implements Strategy {

	private ArrayDeque<Payment> paymentsCopy;

	private LocalDate now;

	private LocalDate getNow() {
		return now;
	}


	public StrategyWhenInvoiceTitleAndPaymentTitleAreNotEqual(ArrayDeque<Payment> paymentsCopy, LocalDate now) {
		this.paymentsCopy = new ArrayDeque<>(paymentsCopy);
		this.now = now;
	}

	/**
	 * Strategy where invoice title and payment title are not equal.
	 * <p>
	 * It checks whether context contain null payment or invoice and it set with value from the input queue of
	 * invoices or payments
	 *
	 * @param context - contains information about payment and invoice
	 * @return true if context contains either payment field of invoice field equal to null, otherwise returns false
	 */
	@Override
	public boolean canExecute(Context context) {
		if (context.getInvoice() == null || context.getPayment() == null) {
			return false;
		}
		return !context.getInvoice().getInvoiceTitle().equals(context.getPayment().getPaymentTitle());
	}

	/**
	 * Strategy where invoice title and payment title are not equal.
	 *
	 * @param context - contains information about payment and invoice
	 * @return pair of values, context and list of outputs
	 *
	 * @pre context cannot contain null values for either invoice or payment. Strategy is executed only
	 * if invoice title es differ than payment title
	 * @post returns new context with if there is no payment in the queue with the same as invoice and interest
	 * in output is  counted up until particular date. Otherwise it search for payment with the same title and
	 * it generates proper output.
	 */
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
			Output output = setupOutput(context.getInvoice(),context.getPayment());
//			output.setDaysOverDeadline(DAYS.between(context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate()));
//			output.setPeriod(payment.getPaymentDate().minusDays(output.getDaysOverDeadline()) + " - " + payment.getPaymentDate());
//			output.setInterestPercentage(decideInterestPercentage(context.getInvoice().getDeadlineDate()));
//			output.setInterest(calculateInterest(context.getInvoice().getDeadlineDate(), context.getInvoice().getAmount(), payment.getPaymentDate()));
			outputs.add(output);
		} else {
			outputs=generateOutputsForFakePayments(context,dates);
			Output output = setupOutput(context.getInvoice(), context.getPayment());
			outputs.add(output);
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

	/**
	 * it calculates how many days payment was over the deadline and then it calculate how much will interest
	 * be for these days
	 *
	 * @param deadlineDate - payment deadline
	 * @param invoice      - amount which has to be paid
	 * @param paymentDate  - actual date of payment
	 * @return - amount of interest which was accumulated because of late payment
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
	 * when the invoice payment deadline was set. So to calculate interest for those delay days, the method creates fake payment
	 * with zero amount for payment and sets the date of payment to the one when interest rate has been changed.
	 *
	 * @param context - contains payment deadline and its actual payment date
	 * @return list of outputs with calculated amount of interest in each sector of interest rate
	 */
	private List<Output> generateOutputsForFakePayments(Context context,List<LocalDate> dates ) {

		List<Output> outputs = new LinkedList<>();
		dates.forEach(date -> {
			Payment payment = new Payment(date.minusDays(1), 0.0);
			Output output = setupOutput(context.getInvoice(), payment);
			Invoice invoice = new Invoice(context.getInvoice().getInvoiceTitle(),date,context.getInvoice().getAmount());
			context.setInvoice(invoice);
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
}

