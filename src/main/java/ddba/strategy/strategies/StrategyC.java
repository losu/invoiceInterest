package ddba.strategy.strategies;

import ddba.Output;
import ddba.Tuple;
import ddba.strategy.Context;
import ddba.strategy.Strategy;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static ddba.InterestPercentage.InterestCalculator.setupOutput;
import static ddba.strategy.InterestCalculationStrategy.datesOfChangedInterestRate;

/**
 * Created by ddba on 10/04/2017.
 */
public class StrategyC implements Strategy {

	@Override
	public boolean canExecute(Context context) {
		if (context.getInvoice() == null || context.getPayment() == null) {
			return false;
		}

		List<LocalDate> dates = datesOfChangedInterestRate(context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());
		if (!dates.isEmpty()) {
			return false;
		}
		return context.getInvoice().getInvoice() > context.getPayment().getPayment();
	}

	@Override
	public Tuple<Context, LinkedList<Output>> execute(Context context) {
		List<Output> outputs = new LinkedList<>();

		if (!canExecute(context)) {
			return null;
		}

		double diff = context.getInvoice().getInvoice()-context.getPayment().getPayment();


		Output output = setupOutput(context.getInvoice(), context.getPayment(), context.getInvoice().getInvoice(), false);

		outputs.add(output);
		Context newContext = context;
		newContext.getInvoice().setInvoice(diff);
		newContext.setPayment(null);

		return new Tuple(newContext, outputs);
	}
}
