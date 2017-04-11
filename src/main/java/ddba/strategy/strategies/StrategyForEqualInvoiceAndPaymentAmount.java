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


public class StrategyForEqualInvoiceAndPaymentAmount implements Strategy {

	@Override
	public boolean canExecute(Context context) {
		if (context.getInvoice() == null || context.getPayment() == null) {
			return false;
		}

		List<LocalDate> dates = datesOfChangedInterestRate(context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());
		if (!dates.isEmpty()) {
			return false;
		}

		return context.getInvoice().getInvoice() == context.getPayment().getPayment();
	}

	@Override
	public Tuple<Context, LinkedList<Output>> execute(Context context) {
		if (!canExecute(context)) {
			return null;
		}
		List<Output> outputs = new LinkedList<>();

		Output output = setupOutput(context.getInvoice(), context.getPayment(), context.getInvoice().getInvoice(), false);

		outputs.add(output);
		Context newContext = new Context();
		return new Tuple(newContext, outputs);
	}
}
