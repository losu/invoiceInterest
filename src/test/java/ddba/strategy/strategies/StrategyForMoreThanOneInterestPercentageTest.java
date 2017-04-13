package ddba.strategy.strategies;

import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import ddba.Tuple;
import ddba.strategy.Context;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class StrategyForMoreThanOneInterestPercentageTest {
	@Test
	public void shouldProduceThreeOutputsWithDifferentInterestPercentageForStrategyD() {
		Context context = new Context();
		context.setInvoice(new Invoice("", LocalDate.of(2000, 1, 10), 1000));
		context.setPayment(new Payment("", LocalDate.of(2001, 12, 17), 500));
		StrategyForMoreThanOneInterestPercentage strategyForMoreThanOneInterestPercentage = new StrategyForMoreThanOneInterestPercentage();

		Tuple<Context, List<Output>> tuple = strategyForMoreThanOneInterestPercentage.execute(context);

		Assertions.assertThat(tuple.getRight().stream().map(Output::getInterestPercentage)).containsExactly(21.0, 30.0, 20.0);
	}
}
