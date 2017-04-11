package ddba.strategy.strategies;

import ddba.Output;
import ddba.Payment;
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
public class StrategyE implements Strategy {

	private LocalDate now;

	public void setNow(LocalDate now) {
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

		//context.setPayment(new Payment(0));

		outputs=generateOutputsForFakePayments(context);

		Output output = setupOutput(
				context.getInvoice(), context.getPayment(), context.getInvoice().getInvoice(), false);
		outputs.add(output);

		double diff = context.getInvoice().getInvoice() - context.getPayment().getPayment();
		Context newContext;
		if (diff == 0.0) {
			newContext = new Context();
		} else {
			newContext = context;
			newContext.getInvoice().setInvoice(diff);
			newContext.setPayment(null);
		}

		return new Tuple(newContext, outputs);
	}

	/**
	 * It creates fake payments to generate proper outputs when interest percentage is being changed
	 *
	 * @param context
	 * @return
	 */
	private  List<Output> generateOutputsForFakePayments(Context context) {
		List<LocalDate> dates = datesOfChangedInterestRate(
				context.getInvoice().getDeadlineDate(), context.getPayment().getPaymentDate());

		List<Output> outputs = new LinkedList<>();
		dates.forEach(date -> {
			Payment payment = new Payment(date, 0.0);
			Output output = setupOutput(context.getInvoice(), payment, context.getInvoice().getInvoice(), true);
			context.getInvoice().setDeadlineDate(date);
			outputs.add(output);
		});
		return outputs;
	}
}

