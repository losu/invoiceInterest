package ddba.strategy;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
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
import ddba.strategy.strategies.StrategyWhenInvoiceTitleAndPaymentTitleAreNotEqual;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InterestCalculation {

	/**
	 * it is overloaded method with added new additional parameter now
	 * <p>
	 * calculates the collection of interests which has to be paid as a fee for the period od time between deadline and
	 * be late actual date of payment. ???
	 * <p>
	 * Method takes two list of data. First one is the whole list of invoices which has to be paid. Second one
	 * is a queue of payments which has information of amount of money paid and when it was paid
	 *
	 * @param invoice  - list of invoices to paid
	 * @param invoice  - list of invoices to paid
	 * @param payments - list of payments
	 * @param now      - when there is now match for title of payment and invoice,
	 *                 it calculates invoice for deadline up until 'now' param
	 * @info now is set as a default date(current date when the report is generated) if there is
	 *                           no information until when the interest should be generated
	 * @return list of Output object which represents period since when to when the payment was not paid, interest percentage
	 * how many days and amount of interest
	 */
	static @NotNull
	List<Output> strategyCalculateInterest(@NotNull ArrayDeque<Invoice> invoice, @NotNull ArrayDeque<Payment> payments, @Nullable LocalDate now) {

//		invoice = Optional.ofNullable(invoice).orElse(Collections.emptyIterator());
//		payments = Optional.ofNullable(payments).orElse(new ArrayDeque<>());
		invoice=Objects.requireNonNull(invoice);
		payments=Objects.requireNonNull(payments);

		List<Output> outputs = new LinkedList<>();

		now = Optional.ofNullable(now).orElse(LocalDate.now());

//		if(now == null){
//			now = LocalDate.now();
//		}

		Context context = new Context();

		List<Strategy> strategies = new ArrayList<>();
		StrategyForSettingNewValuesWhenFieldIsNull strategyForSettingNewValuesWhenFieldIsNull = new StrategyForSettingNewValuesWhenFieldIsNull(invoice, payments);

		StrategyForEqualInvoiceAndPaymentAmount strategyForEqualInvoiceAndPaymentAmount = new StrategyForEqualInvoiceAndPaymentAmount();
		StrategyForInvoiceBiggerThanInvoiceAmount strategyForInvoiceBiggerThanInvoiceAmount = new StrategyForInvoiceBiggerThanInvoiceAmount();
		StrategyForMoreThanOneInterestPercentage strategyForMoreThanOneInterestPercentage = new StrategyForMoreThanOneInterestPercentage();
		StrategyWhenInvoiceTitleAndPaymentTitleAreNotEqual strategyWhenThereIsNoPaymentTitle = new StrategyWhenInvoiceTitleAndPaymentTitleAreNotEqual(payments,now);

		strategies.add(strategyForSettingNewValuesWhenFieldIsNull);
		strategies.add(strategyForEqualInvoiceAndPaymentAmount);
		strategies.add(strategyForInvoiceBiggerThanInvoiceAmount);
		strategies.add(strategyForMoreThanOneInterestPercentage);
		strategies.add(strategyWhenThereIsNoPaymentTitle);

		Tuple<Context, List<Output>> tuple = null;

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
