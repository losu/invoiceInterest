package ddba.strategy;

import ddba.InterestRate;
import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.StatutoryInterest;
import ddba.Tuple;
import ddba.strategy.strategies.StrategyForEqualInvoiceAndPaymentAmount;
import ddba.strategy.strategies.StrategyForInvoiceBiggerThanInvoiceAmount;
import ddba.strategy.strategies.StrategyForMoreThanOneInterestPercentage;
import ddba.strategy.strategies.StrategyForSettingNewValuesWhenFieldIsNull;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InterestCalculation {

	public static List<Output> strategyCalculateInterest(ArrayDeque<Invoice> invoice, ArrayDeque<Payment> payments, LocalDate now) {

		List<Output> outputs = new LinkedList<>();

//			Context context = new Context(invoice.pollFirst(),payments.pollFirst());
		Context context = new Context();

		List<Strategy> strategies = setupStrategies();
		StrategyForSettingNewValuesWhenFieldIsNull strategyForSettingNewValuesWhenFieldIsNull = new StrategyForSettingNewValuesWhenFieldIsNull(invoice, payments);

		strategies.add(strategyForSettingNewValuesWhenFieldIsNull);
		Tuple<Context, LinkedList<Output>> tuple = null;

		boolean flag = true;
		int counter = 0;
		while (flag) {
			for (Strategy strategy : strategies) {
				if (strategy.canExecute(context)) {
					tuple = strategy.execute(context);
					context = tuple.getLeft();
					counter = 0;
				} else {
					counter++;
				}

				if (tuple != null) {
					tuple.getRight().forEach(outputs::add);
					tuple = null;
				}
				if (counter == strategies.size()) {
					flag = false;
					break;
				}
			}
		}

		return outputs;
	}

	/**
	 * it checks if between invoice payment deadline and actual date of payment
	 * there were changes of the interestRate. If so then it save those dates.
	 *
	 *
	 * @param paymentDeadline - date of invoice payment deadline
	 * @param actualPaymentDate - date of payment
	 * @return - empty list if no changes in interestRate were done. List of date if  between deadline and payment were
	 * occured some changes of interestRate
	 */
	public static List<LocalDate> datesOfChangedInterestRate(LocalDate paymentDeadline, LocalDate actualPaymentDate) {
		InterestRate interestRate = new InterestRate();
		List<LocalDate> dates = new ArrayList<>();
		for (StatutoryInterest statutoryInterest : interestRate.getListOFInterest()) {
			if (statutoryInterest.getDate().isAfter(paymentDeadline) && statutoryInterest.getDate().isBefore(actualPaymentDate)) {
				dates.add(statutoryInterest.getDate());
			}
		}
		return dates;
	}

	private static List<Strategy> setupStrategies() {
		List<Strategy> strategies = new LinkedList<>();

		StrategyForEqualInvoiceAndPaymentAmount strategyForEqualInvoiceAndPaymentAmount = new StrategyForEqualInvoiceAndPaymentAmount();
		StrategyForInvoiceBiggerThanInvoiceAmount strategyForInvoiceBiggerThanInvoiceAmount = new StrategyForInvoiceBiggerThanInvoiceAmount();
		StrategyForMoreThanOneInterestPercentage strategyForMoreThanOneInterestPercentage = new StrategyForMoreThanOneInterestPercentage();

		strategies.add(strategyForEqualInvoiceAndPaymentAmount);
		strategies.add(strategyForInvoiceBiggerThanInvoiceAmount);
		strategies.add(strategyForMoreThanOneInterestPercentage);
		return strategies;
	}
}
