package ddba.strategy.strategies;

import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.Tuple;
import ddba.strategy.Context;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public class StrategyWhenThereIsNoPaymentTitleTest {

	@Test
	public void should() {

		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(new Invoice("A", LocalDate.of(2000, 1, 10), 1000)));
		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(
						new Payment("", LocalDate.of(2000, 1, 11), 1000),
						new Payment("A", LocalDate.of(2000, 1, 13), 1000),
						new Payment("D", LocalDate.of(2000, 1, 14), 1000)
				)
		);

		Context context = new Context();
		context.setInvoice(new Invoice("A", LocalDate.of(2000, 1, 10), 1000));
		context.setPayment(new Payment("", LocalDate.of(2001, 1, 11), 500));
		LocalDate now = LocalDate.of(2000, 1, 12);
		StrategyWhenThereIsNoPaymentTitle strategy = new StrategyWhenThereIsNoPaymentTitle(payments,now);

		Tuple<Context, List<Output>> tuple = strategy.execute(context);

		tuple.getRight().forEach(s-> System.out.println(s.getInterest()));

		//Assertions.assertThat(tuple.getRight().stream().map(Output::getInterestPercentage)).containsExactly(21.0, 30.0, 20.0);
	}
}

