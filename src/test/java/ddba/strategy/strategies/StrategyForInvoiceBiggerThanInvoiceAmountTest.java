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


public class StrategyForInvoiceBiggerThanInvoiceAmountTest {
	@Test
	public void shouldConsumePaymentStrategyC() {
		Context context = new Context();
		context.setInvoice(new Invoice("", LocalDate.of(2000, 1, 10), 1000));
		context.setPayment(new Payment("", LocalDate.of(2000, 1, 11), 500));
		StrategyForInvoiceBiggerThanInvoiceAmount strategyForInvoiceBiggerThanInvoiceAmount = new StrategyForInvoiceBiggerThanInvoiceAmount();

		Tuple<Context, List<Output>> tuple = strategyForInvoiceBiggerThanInvoiceAmount.execute(context);

		Assertions.assertThat(tuple.getLeft().getPayment()).isNull();
		Assertions.assertThat(tuple.getLeft().getInvoice().getInvoice()).isEqualTo(500.0);
		Assertions.assertThat(tuple.getRight().stream().map(Output::getInterest)).containsExactly(0.58);
	}
}
