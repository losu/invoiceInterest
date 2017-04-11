package ddba;

import ddba.strategy.Context;
import ddba.strategy.InterestCalculationStrategy;
import ddba.strategy.strategies.StrategyA;
import ddba.strategy.strategies.StrategyB;
import ddba.strategy.strategies.StrategyC;
import ddba.strategy.strategies.StrategyD;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ddba on 10/04/2017.
 */
public class InteresCalculationStrategyTest {

	@Test
	public void shouldContextFieldsNotBeNullAfterExecutingStrategyA() {
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(new Invoice("", LocalDate.of(2000, 1, 10), 1000)));
		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(
						new Payment("", LocalDate.of(2000, 2, 10), 1000)
				)
		);

		Context context = new Context();

		StrategyA strategyA = new StrategyA();
		strategyA.setInvoiceCopy(invoices);
		strategyA.setPaymentsCopy(payments);

		Tuple<Context, LinkedList<Output>> tuple = strategyA.execute(context);
		context = tuple.getLeft();

		Assertions.assertThat(context.getInvoice()).isNotNull();
		Assertions.assertThat(context.getPayment()).isNotNull();
	}

	@Test
	public void shouldConsumeContextAndReturnOutputForStrategyB() {

		Context context = new Context();
		context.setInvoice(new Invoice("", LocalDate.of(2000, 1, 10), 1000));
		context.setPayment(new Payment("", LocalDate.of(2000, 1, 11), 1000));
		StrategyB strategyB = new StrategyB();

		Tuple<Context, LinkedList<Output>> tuple = strategyB.execute(context);

		Assertions.assertThat(tuple.getLeft().getPayment()).isNull();
		Assertions.assertThat(tuple.getLeft().getInvoice()).isNull();
		Assertions.assertThat(tuple.getRight().stream().map(Output::getInterest)).containsExactly(0.58);
	}

	@Test
	public void shouldConsumePaymentStrategyC() {
		Context context = new Context();
		context.setInvoice(new Invoice("", LocalDate.of(2000, 1, 10), 1000));
		context.setPayment(new Payment("", LocalDate.of(2000, 1, 11), 500));
		StrategyC strategyC = new StrategyC();

		Tuple<Context, LinkedList<Output>> tuple = strategyC.execute(context);

		Assertions.assertThat(tuple.getLeft().getPayment()).isNull();
		Assertions.assertThat(tuple.getLeft().getInvoice().getInvoice()).isEqualTo(500.0);
		Assertions.assertThat(tuple.getRight().stream().map(Output::getInterest)).containsExactly(0.58);
	}

	@Test
	public void shouldProduceThreeOutputsWithDifferentInterestPercentageForStrategyD() {
		Context context = new Context();
		context.setInvoice(new Invoice("", LocalDate.of(2000, 1, 10), 1000));
		context.setPayment(new Payment("", LocalDate.of(2001, 12, 17), 500));
		StrategyD strategyD = new StrategyD();

		Tuple<Context, LinkedList<Output>> tuple = strategyD.execute(context);

		Assertions.assertThat(tuple.getRight().stream().map(Output::getInterestPercentage)).containsExactly(21.0, 30.0,20.0);
	}


	@Test
	public void shouldRunProperlyTheWholeFlowByUsingAllStrategies(){
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(
						new Invoice(LocalDate.of(2000, 2, 10), 1000),
						new Invoice(LocalDate.of(2000, 4, 20), 1000)));

		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(new Payment(LocalDate.of(2000, 2, 20), 500),
						new Payment(LocalDate.of(2000, 3, 20), 500),
						new Payment(LocalDate.of(2000, 5, 10), 500),
						new Payment(LocalDate.of(2000, 12, 12), 500)));

		List<Output> outputs;
		outputs = InterestCalculationStrategy.strategyCalculateInterest(invoices, payments, null);

		Assertions.assertThat(outputs.stream().map(Output::getInterestPercentage)).containsExactly(21.0, 21.0, 21.0, 21.0, 30.0);
		Assertions.assertThat(outputs.stream().map(Output::getInterest)).containsExactly(5.75, 11.22, 11.51, 55.81, 16.85);
		Assertions.assertThat(outputs.stream().map(Output::getDaysOverDeadline)).containsExactly(10L, 39L, 20L, 194L,41L);

	}
}
