package ddba.strategy;

import ddba.InterestPercentage;
import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.StatutoryInterest;
import ddba.Tuple;
import ddba.strategy.strategies.StrategyA;
import ddba.strategy.strategies.StrategyB;
import ddba.strategy.strategies.StrategyC;
import ddba.strategy.strategies.StrategyD;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ddba on 10/04/2017.
 */
public class InterestCalculationStrategy {

	public static List<Output> strategyCalculateInterest(ArrayDeque<Invoice> invoice, ArrayDeque<Payment> payments, LocalDate now) {

		List<Output> outputs = new LinkedList<>();

//			Context context = new Context(invoice.pollFirst(),payments.pollFirst());
		Context context = new Context();

		List<Strategy> strategies = setupStrategies();
		StrategyA strategyA = new StrategyA();
		strategyA.setInvoiceCopy(invoice);
		strategyA.setPaymentsCopy(payments);
		strategies.add(strategyA);
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

//		StrategyB strategyB = new StrategyB();
//		StrategyC strategyC = new StrategyC();
//		StrategyD strategyD = new StrategyD();
		//	StrategyE strategyE = new StrategyE();
		//	strategyE.setNow(now);

//		if (strategyA.canExecute(context)) {
//			tuple=strategyA.execute(context);
//			context = tuple.getLeft();
//		}
//		if (strategyB.canExecute(context)) {
//			tuple=strategyB.execute(context);
//			context = tuple.getLeft();
//		}
//		tuple.getRight().forEach(outputs::add);
//
//		if (strategyC.canExecute(context)) {
//			tuple=strategyC.execute(context);
//			context = tuple.getLeft();
//		}
//		tuple.getRight().forEach(outputs::add);
//		if (strategyB.canExecute(context)) {
//			tuple=strategyB.execute(context);
//			context = tuple.getLeft();
//		}
//		tuple.getRight().forEach(outputs::add);
//
//		if (strategyD.canExecute(context)) {
//			tuple=strategyD.execute(context);
//			context = tuple.getLeft();
//		}
//		tuple.getRight().forEach(outputs::add);


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

		StrategyB strategyB = new StrategyB();
		StrategyC strategyC = new StrategyC();
		StrategyD strategyD = new StrategyD();

		strategies.add(strategyB);
		strategies.add(strategyC);
		strategies.add(strategyD);
		return strategies;
	}
}
