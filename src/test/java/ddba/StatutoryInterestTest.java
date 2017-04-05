package ddba;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by ddba on 29/03/2017.
 */
@RunWith(JUnitParamsRunner.class)
public class StatutoryInterestTest {

	InterestPercentage interestPercentage = new InterestPercentage();

	private Object[] invoice() {
		return new Object[]{new Invoice(LocalDate.of(2000, 1, 10), 1000), (LocalDate.of(2000, 2, 20))};
	}

	private Object[] dates() {
		return new Object[]{
				LocalDate.of(2000, 2, 10),
				LocalDate.of(2000, 2, 20)
		};
	}

	@Test
	public void shouldChooseProperInterestPercentageToCalculateInterest() {

		LocalDate paymentDate = LocalDate.of(2000, 2, 20);

		double interestPer = interestPercentage.decideInterestPercentage(paymentDate);

		Assertions.assertThat(interestPer).isEqualTo(21.00);
	}

	@Test
	public void shouldChooseTheLatestUpdatedInteresPercentage() {

		LocalDate paymentDate = LocalDate.of(2017, 2, 20);

		double interestPer = interestPercentage.decideInterestPercentage(paymentDate);

		Assertions.assertThat(interestPer).isEqualTo(5.00);
	}

	@Test
	@Parameters(method = "dates")
	public void shouldCalculateHowManyDaysAreOnInterest(LocalDate deadlineDate, LocalDate paymentDate) {
		long numberOfDays = DAYS.between(deadlineDate, paymentDate);

		Assertions.assertThat(numberOfDays).isEqualTo(10L);
	}

	@Test
	@Parameters(method = "invoice")
	public void shouldReturnTheCalculatedInterestWhichHasToBePaid(Invoice invoice, LocalDate paymentDate) {

		double interest = InterestPercentage.InterestCalculator.calculateInterest(invoice.getDeadlineDate(), invoice.getInvoice(), paymentDate);
		Assertions.assertThat(interest).isEqualTo(23.59);
	}

//	@Test
//	public void shouldReturnTwoInterestSinceInvoicePaidInTwoPartsBothOverDeadline() {
//		Invoice invoice = new Invoice(LocalDate.of(2000, 2, 10), 1000);
//		List<Payment> payments =
//				new LinkedList<>(Arrays.asList(new Payment(LocalDate.of(2000, 2, 20), 500),
//						new Payment(LocalDate.of(2000, 3, 20), 500)));
//
//		List<Output> outputs;
//		outputs = InterestPercentage.InterestCalculator.calculateInterest(invoice, payments);
//
//		Assertions.assertThat(outputs.get(0).getInterest()).isEqualTo(5.75);
//		Assertions.assertThat(outputs.get(1).getInterest()).isEqualTo(11.22);
//	}

	@Test
	public void shouldReturnFourInterestsInterestsSinceArePaidAfterDeadline() {
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(
						new Invoice(LocalDate.of(2000, 2, 10), 1000),
						new Invoice(LocalDate.of(2000, 4, 20), 1000)));

		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(new Payment(LocalDate.of(2000, 2, 20), 500),
						new Payment(LocalDate.of(2000, 3, 20), 500),
						new Payment(LocalDate.of(2000, 5, 10), 500),
						new Payment(LocalDate.of(2000, 5, 12), 500)));

		List<Output> outputs;
		outputs = InterestPercentage.InterestCalculator.calculateInterest(invoices, payments,null);

		Assertions.assertThat(outputs.stream().map(Output::getInterestPercentage)).containsExactly(21.0, 21.0, 21.0, 21.0);
		Assertions.assertThat(outputs.stream().map(Output::getInterest)).containsExactly(5.75, 11.22, 11.51, 6.33);
		Assertions.assertThat(outputs.stream().map(Output::getDaysOverDeadline)).containsExactly(10L, 39L, 20L, 22L);

	}

	@Test
	public void shouldReturnTwoDifferPercentageInterestForOnePayment() {
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(new Invoice(LocalDate.of(2000, 1, 10), 1000)));

		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(new Payment(LocalDate.of(2000, 12, 10), 1000)));

		List<Output> outputs;
		outputs = InterestPercentage.InterestCalculator.calculateInterest(invoices, payments,null);

		Assertions.assertThat(outputs.stream().map(Output::getInterestPercentage)).containsExactly(21.0, 30.0);
	}

	@Test
	public void shouldNOTPaymentBeDoneForTheParticularInvoiceSinceTitleDoesNotMatch() {
		ArrayDeque<Invoice> invoices = new ArrayDeque<>(
				Arrays.asList(new Invoice("A rent", LocalDate.of(2000, 1, 10), 1000)));
		ArrayDeque<Payment> payments = new ArrayDeque<>(
				Arrays.asList(
						new Payment("A rent", LocalDate.of(2000, 1, 11), 1000)
				)
		);

		List<Output> outputs = InterestPercentage.InterestCalculator.calculateInterest(invoices, payments,null);

		outputs.forEach(output -> System.out.println(output.getDaysOverDeadline()));
		Assertions.assertThat(outputs).isEmpty();

	}

	@Test
	public void test() {
		LocalDate of = LocalDate.of(2000, 2, 10);
		if (of.isEqual(LocalDate.of(2000, 2, 10))) {
			System.out.println("t");
		}
		System.out.println("f");
		System.out.println(DAYS.between(LocalDate.of(2000, 1, 10), LocalDate.of(2000, 1, 10)));
		System.out.println(DAYS.between(LocalDate.of(2000, 11, 1), LocalDate.of(2000, 12, 10)));
	}
}
