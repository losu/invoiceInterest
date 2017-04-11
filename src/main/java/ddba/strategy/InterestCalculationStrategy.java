package ddba.strategy;

import ddba.InterestPercentage;
import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.StatutoryInterest;
import ddba.Tuple;
import ddba.strategy.strategies.StrategyForSettingNewValuesWhenFieldIsNull;
import ddba.strategy.strategies.StrategyForEqualInvoiceAndPaymentAmount;
import ddba.strategy.strategies.StrategyForInvoiceBiggerThanInvoiceAmount;
import ddba.strategy.strategies.StrategyForMoreThanOneInterestPercentage;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class InterestCalculationStrategy {

	public static List<Output> strategyCalculateInterest(ArrayDeque<Invoice> invoice, ArrayDeque<Payment> payments, LocalDate now) {

		List<Output> outputs = new LinkedList<>();

//			Context context = new Context(invoice.pollFirst(),payments.pollFirst());
		Context context = new Context();

		List<Strategy> strategies = setupStrategies();
		StrategyForSettingNewValuesWhenFieldIsNull strategyForSettingNewValuesWhenFieldIsNull = new StrategyForSettingNewValuesWhenFieldIsNull(invoice,payments);

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
					tuple=null;
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
	 * it checks how many changes in interest rate were and returns all of those dates (where interestRate were changed)
	 *
	 * @param start - invoice date
	 * @param end   - payment date
	 * @return
	 */
	public static List<LocalDate> datesOfChangedInterestRate(LocalDate start, LocalDate end) {
		InterestPercentage interestPercentage = new InterestPercentage();
		List<LocalDate> dates = new ArrayList<>();
		for (StatutoryInterest statutoryInterest : interestPercentage.getListOFInterest()) {
			if (statutoryInterest.getDate().isAfter(start) && statutoryInterest.getDate().isBefore(end)) {
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
