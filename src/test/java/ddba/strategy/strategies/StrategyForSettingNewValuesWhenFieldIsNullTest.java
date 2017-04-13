package ddba.strategy.strategies;

import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.Tuple;
import ddba.strategy.Context;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public class StrategyForSettingNewValuesWhenFieldIsNullTest {
	@Test
	public void shouldContext() {
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(new Invoice("", LocalDate.of(2000, 1, 10), 1000)));
		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(
						new Payment("", LocalDate.of(2000, 2, 10), 1000)
				)
		);

		Context context = new Context();

		StrategyForSettingNewValuesWhenFieldIsNull strategyForSettingNewValuesWhenFieldIsNull = new StrategyForSettingNewValuesWhenFieldIsNull(invoices,payments);

		Tuple<Context, List<Output>> tuple = strategyForSettingNewValuesWhenFieldIsNull.execute(context);
		context = tuple.getLeft();

		Assertions.assertThat(context.getInvoice()).isNotNull();
		Assertions.assertThat(context.getPayment()).isNotNull();
	}
}
