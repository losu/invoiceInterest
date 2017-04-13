package ddba.strategy;

import ddba.Invoice;
import ddba.Output;
import ddba.Payment;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;


public class InterestCalculationTest {

	@Test
	public void shouldRunProperlyTheWholeFlowByUsingAllStrategies(){
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(
						new Invoice("Car",LocalDate.of(2000, 2, 10), 1000),
						new Invoice("Apratment",LocalDate.of(2000, 4, 20), 1000),
						new Invoice("Phone",LocalDate.of(2000, 4, 21), 1000)
				)
		);

		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(new Payment("Car",LocalDate.of(2000, 2, 20), 500),
						new Payment("Car",LocalDate.of(2000, 3, 20), 500),
						new Payment("Apratment",LocalDate.of(2000, 5, 10), 500),
						new Payment("Apratment",LocalDate.of(2000, 12, 12), 500),
						new Payment("Water",LocalDate.of(2000, 12, 12), 500)
				));

		List<Output> outputs;
		outputs = InterestCalculation.strategyCalculateInterest(invoices, payments, LocalDate.of(2000, 4, 22));

		Assertions.assertThat(outputs.stream().map(Output::getInterestPercentage)).containsExactly(21.0, 21.0, 21.0, 21.0, 30.0,21.0);
		Assertions.assertThat(outputs.stream().map(Output::getInterest)).containsExactly(5.75, 11.22, 11.51, 55.81, 16.85,0.58);
		Assertions.assertThat(outputs.stream().map(Output::getDaysOverDeadline)).containsExactly(10L, 39L, 20L, 194L,41L,1L);

	}
}
