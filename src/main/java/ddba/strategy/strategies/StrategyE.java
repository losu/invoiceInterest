package ddba.strategy.strategies;

import ddba.Output;
import ddba.Payment;
import ddba.Tuple;
import ddba.strategy.Context;
import ddba.strategy.Strategy;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;

import static ddba.InterestPercentage.InterestCalculator.setupOutput;
import static ddba.strategy.InterestCalculation.datesOfChangedInterestRate;

public class StrategyE implements Strategy {

	private ArrayDeque<Payment> paymentsCopy;

	private LocalDate now;

	private LocalDate getNow() {
		return now;
	}

	public ArrayDeque<Payment> getPaymentsCopy() {
		return paymentsCopy;
	}

	public StrategyE(ArrayDeque<Payment> paymentsCopy, LocalDate now) {
		this.paymentsCopy = new ArrayDeque<>(paymentsCopy);
		this.now = now;
	}

	@Override
	public boolean canExecute(Context context) {

		return !context.getInvoice().getInvoiceTitle().equals(context.getPayment().getPaymentTitle());
	}

	@Override
	public Tuple<Context, LinkedList<Output>> execute(Context context) {
		if (!canExecute(context)) {
			return null;
		}

		List<Output> outputs = new LinkedList<>();
		Context newContext = context;
		Payment payment = findPaymentWithProperTitle(context.getInvoice().getInvoiceTitle());
		if (payment == null) {
			payment = new Payment(getNow(),0.0);
		}
		newContext.setPayment(payment);

		List<LocalDate> dates = datesOfChangedInterestRate(
				newContext.getInvoice().getDeadlineDate(), newContext.getPayment().getPaymentDate());

		if (dates.isEmpty()) {
			Output output = setupOutput(context.getInvoice(), context.getPayment(), context.getInvoice().getInvoice(), false);
			outputs.add(output);
		} else {
			StrategyForMoreThanOneInterestPercentage str = new StrategyForMoreThanOneInterestPercentage();
			//str.generateOutputsForFakePayments(newContext);
		}

		return new Tuple(newContext, outputs);
	}

	private Payment findPaymentWithProperTitle(String title) {
		boolean flag = false;
		Payment payment = null;
		for (int i = 0; i < paymentsCopy.size(); ) {
			Payment paymentTemp = paymentsCopy.pollFirst();
			if (title.equals(paymentTemp.getPaymentTitle())) {
				flag = true;
				payment = paymentTemp;
			} else {
				paymentsCopy.addLast(paymentTemp);
			}
		}
		return flag ? payment : null;
	}
}

