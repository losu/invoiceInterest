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
	public void shouldRunProperlyTheWholeFlowByUsingAllStrategies() {
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(
						new Invoice("Car", LocalDate.of(2000, 2, 10), 1000),
						new Invoice("Apartment", LocalDate.of(2000, 4, 20), 1000),
						new Invoice("Phone", LocalDate.of(2000, 4, 21), 1000)
				)
		);

		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(
						new Payment("Car", LocalDate.of(2000, 2, 20), 500),
						new Payment("Car", LocalDate.of(2000, 3, 20), 500),
						new Payment("Apartment", LocalDate.of(2000, 5, 10), 500),
						new Payment("Apartment", LocalDate.of(2000, 12, 12), 500),
						new Payment("Water", LocalDate.of(2000, 12, 12), 500)
				));

		List<Output> outputs;
		outputs = InterestCalculation.strategyCalculateInterest(invoices, payments, LocalDate.of(2000, 12, 22));

		Assertions.assertThat(outputs.stream().map(Output::getInterestPercentage)).containsExactly(21.0, 21.0, 21.0, 21.0, 30.0, 21.0, 30.0);
		Assertions.assertThat(outputs.stream().map(Output::getInterest)).containsExactly(5.75, 11.22, 11.51, 55.81, 17.26, 111.04, 42.74);
		Assertions.assertThat(outputs.stream().map(Output::getDaysOverDeadline)).containsExactly(10L, 39L, 20L, 194L, 42L, 193L, 52L);
	}

	@Test
	public void shouldCalculatesProperlyEvenWhenTitlesOfPaymentAreNotInProperOrder() {
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(
						new Invoice("Car", LocalDate.of(2000, 2, 10), 1000),
						new Invoice("Apartment", LocalDate.of(2000, 2, 20), 1000),
						new Invoice("Phone", LocalDate.of(2000, 3, 1), 1000)
				)
		);

		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(
						new Payment("Apartment", LocalDate.of(2000, 2, 22), 1000),
						new Payment("Car", LocalDate.of(2000, 2, 11), 1000),
						new Payment("Phone", LocalDate.of(2000, 3, 10), 1000)
				));

		List<Output> outputs;
		outputs = InterestCalculation.strategyCalculateInterest(invoices, payments, LocalDate.of(2000, 4, 22));

		outputs.forEach(s -> System.out.println(s.getPeriod() + " " + " " + s.getInterest()));

		Assertions.assertThat(outputs.stream().map(Output::getInterestPercentage)).containsExactly(21.0, 21.0, 21.0);
		Assertions.assertThat(outputs.stream().map(Output::getInterest)).containsExactly(0.58, 1.15, 5.18);
		Assertions.assertThat(outputs.stream().map(Output::getDaysOverDeadline)).containsExactly(1L, 2L, 9L);
		Assertions.assertThat(outputs.stream().map(Output::getPeriod)).containsExactly(
				"2000-02-10 - 2000-02-11", "2000-02-20 - 2000-02-22", "2000-03-01 - 2000-03-10");
	}
}
