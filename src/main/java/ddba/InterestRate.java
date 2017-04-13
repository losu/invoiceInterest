package ddba;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class InterestRate {
	private static List<StatutoryInterest> listOFInterests;

	public InterestRate() {
		listOFInterests = new LinkedList<>();

		listOFInterests.add(new StatutoryInterest(LocalDate.of(1990, 1, 1), 720.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1990, 2, 1), 480.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1990, 3, 1), 216.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1990, 4, 1), 216.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1990, 5, 1), 144.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1990, 7, 1), 60.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1990, 12, 1), 90.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1991, 3, 1), 140.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1991, 9, 15), 80.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1992, 8, 15), 60.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1993, 5, 1), 54.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1995, 12, 15), 46.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1997, 1, 1), 35.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1998, 4, 15), 33.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1999, 2, 1), 24.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(1999, 5, 15), 21.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2000, 11, 1), 30.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2001, 12, 15), 20.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2002, 7, 25), 16.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2003, 2, 1), 13.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2003, 9, 25), 12.25));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2005, 1, 10), 13.50));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2005, 10, 15), 11.50));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2008, 12, 15), 13.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2014, 12, 23), 8.00));
		listOFInterests.add(new StatutoryInterest(LocalDate.of(2016, 1, 1), 5.00));
	}

	public List<StatutoryInterest> getListOFInterest() {
		return listOFInterests;
	}

	/**
	 * it checks what was the interest rate in that date
	 *
	 * @param date - given date which is being checked
	 * @return - double value of interest percentage
	 */
	public static double decideInterestPercentage(LocalDate date) {
		if (date.isAfter(listOFInterests.get(listOFInterests.size() - 1).getDate()) || date.isEqual(listOFInterests.get(listOFInterests.size() - 1).getDate()))
			return listOFInterests.get(listOFInterests.size() - 1).getInterestPercentage();
		for (int i = 0; i < listOFInterests.size(); i++) {
			if ((date.isAfter(listOFInterests.get(i).getDate()) || date.isEqual(listOFInterests.get(i).getDate()))
					&& date.isBefore(listOFInterests.get(i + 1).getDate())) {
				return listOFInterests.get(i).getInterestPercentage();
			}
		}
		return 0;
	}

	public static class InterestCalculator {

		/**
		 * it calculates how many days payment was over the deadline and then it calculate how much will interest
		 * be for these days
		 *
		 * @param deadlineDate - payment deadline
		 * @param invoice      - amount which has to be paid
		 * @param paymentDate  - actual date of payment
		 * @return - amount of interest which was comulated because of late payment
		 */
		static double calculateInterest(LocalDate deadlineDate, double invoice, LocalDate paymentDate) {

			double percentage = decideInterestPercentage(deadlineDate);
			double annualInterest = invoice * percentage / 100;

			double dailyInterest = annualInterest / 365;
			long numberOfDays = DAYS.between(deadlineDate, paymentDate);

			double interest = dailyInterest * numberOfDays;

			//rounding to decimal places
			interest = Math.round(interest * 100);
			interest = interest / 100;

			return interest;
		}

//		/**
//		 * it checks if between invoice payment deadline and actual date of payment
//		 * there were changes of the interestRate. If so then it save those dates.
//		 *
//		 * @param paymentDeadline   - date of invoice payment deadline
//		 * @param actualPaymentDate - date of payment
//		 * @return - empty list if no changes in interestRate were done. List of date if  between deadline and payment were
//		 * occurred some changes of interestRate
//		 */
//		public static List<LocalDate> datesOfChangedInterestRate(LocalDate paymentDeadline, LocalDate actualPaymentDate) {
//			InterestRate interestRate = new InterestRate();
//			List<LocalDate> dates = new ArrayList<>();
//			for (StatutoryInterest statutoryInterest : interestRate.getListOFInterest()) {
//				if (statutoryInterest.getDate().isAfter(paymentDeadline) && statutoryInterest.getDate().isBefore(actualPaymentDate)) {
//					dates.add(statutoryInterest.getDate());
//				}
//			}
//			return dates;
//		}

		/**
		 * it checks if between invoice payment deadline and actual date of payment
		 * there were changes of the interestRate. If so then it save those dates.
		 *
		 * @param paymentDeadline   - date of invoice payment deadline
		 * @param actualPaymentDate - date of payment
		 * @return returning null if no date was between or it returns LocalDate type if it founds
		 * a date between deadline and payment
		 */
		private static LocalDate decide(LocalDate paymentDeadline, LocalDate actualPaymentDate) {
			//List<LocalDate> dates = new ArrayList<>();
			for (StatutoryInterest statutoryInterest : listOFInterests) {
				if (statutoryInterest.getDate().isAfter(paymentDeadline) && statutoryInterest.getDate().isBefore(actualPaymentDate)) {
					//dates.add(statutoryInterest.getDate());
					return statutoryInterest.getDate();
				}
			}
			return null;
		}

		private static Output setupOutput(Invoice invoice, Payment payment, double invoiceTemp) {
			Output output = new Output();
			output.setDaysOverDeadline(DAYS.between(invoice.getDeadlineDate(), payment.getPaymentDate()));
			output.setPeriod(payment.getPaymentDate().minusDays(output.getDaysOverDeadline()) + " - " + payment.getPaymentDate());
			output.setInterestPercentage(decideInterestPercentage(invoice.getDeadlineDate()));
			output.setInterest(calculateInterest(invoice.getDeadlineDate(), invoiceTemp, payment.getPaymentDate()));

			return output;
		}

		/**
		 * it calculates the interest which has to be paid as a fee for the period od time between deadline and
		 * be late actual date of payment.
		 * <p>
		 * Method takes two list of data. First one is the whole list of invoices which has to be paid. Second one
		 * is a queue of payments which has information of amount of money paid and when it was paid
		 *
		 * @param invoice  - list of invoices to paid
		 * @param payments - list of payments
		 * @return list of Output object which represents period since when to when the payment was not paid, interest percentage
		 * how many days and amount of interest
		 */
		public static List<Output> calculateInterest(ArrayDeque<Invoice> invoice, ArrayDeque<Payment> payments) {
			List<Invoice> invoiceCopy = new LinkedList<>(invoice);
			List<Payment> paymentsCopy = new LinkedList<>(payments);

			List<Output> outputs = new LinkedList<>();
			for (int i = 0; i < invoiceCopy.size(); i++) {
				double invoiceTemp = invoice.getFirst().getInvoice();
				double diff = 0.0;
				for (int j = 0; j < paymentsCopy.size(); j++) {

					LocalDate date = decide(invoiceCopy.get(i).getDeadlineDate(), paymentsCopy.get(j).getPaymentDate());
					if (date != null) {
						Payment payment = new Payment(date, 0.0);
						paymentsCopy.add(j, payment);
					}
					diff = invoiceTemp - paymentsCopy.get(j).getPayment();

					if (diff < 0.0) {
						break;
					}
					if (invoiceCopy.get(i).getInvoiceTitle().equals(paymentsCopy.get(j).getPaymentTitle())) {
						Payment payment = paymentsCopy.get(j);
						paymentsCopy.remove(j);
						j = -1;

						if (diff == invoiceTemp) {
							Output output = setupOutput(invoice.getFirst(), payment, invoiceTemp);
							outputs.add(output);
							double invoiceCost = invoiceCopy.get(i).getInvoice();
							invoiceCopy.remove(i);
							invoiceCopy.add(new Invoice(date, invoiceCost));
							break;
						}

						Output output = setupOutput(invoiceCopy.get(i), payment, invoiceTemp);
						outputs.add(output);
						invoiceTemp = invoiceTemp - payment.getPayment();
						//	}
					}
					if (paymentsCopy.size() - 1 == j && paymentsCopy.size() > 0 && invoiceCopy.size() > 0) {
						j = -1;
						//	break;
					}
				}
				if (diff != invoiceTemp && diff < 0.0) {
					invoiceCopy.remove(i);
				}
				if (invoiceCopy.size() - 1 == i && invoiceCopy.size() > 0 && paymentsCopy.size() > 0) {
					i = -1;
				}
			}
			return outputs;
		}

		/**
		 * it is overloaded method with added new additional parameter now
		 * <p>
		 * calculates the interest which has to be paid as a fee for the period od time between deadline and
		 * be late actual date of payment.
		 * <p>
		 * Method takes two list of data. First one is the whole list of invoices which has to be paid. Second one
		 * is a queue of payments which has information of amount of money paid and when it was paid
		 *
		 * @param invoice  - list of invoices to paid
		 * @param payments - list of payments
		 * @param now      - when there is now match for title of payment and invoice,
		 *                 it calculates invoice for deadline up until 'now' param
		 * @return list of Output object which represents period since when to when the payment was not paid, interest percentage
		 * how many days and amount of interest
		 */
		static List<Output> calculateInterest(ArrayDeque<Invoice> invoice, ArrayDeque<Payment> payments, LocalDate now) {
			List<Invoice> invoiceCopy = new LinkedList<>(invoice);
			List<Payment> paymentsCopy = new LinkedList<>(payments);

			List<Output> outputs = new LinkedList<>();
			for (int i = 0; i < invoiceCopy.size(); i++) {
				double invoiceTemp = invoiceCopy.get(i).getInvoice();
				double diff = 0.0;
				for (int j = 0; j < paymentsCopy.size(); j++) {

					LocalDate date = decide(invoiceCopy.get(i).getDeadlineDate(), paymentsCopy.get(j).getPaymentDate());
					if (date != null) {
						Payment payment = new Payment(date, 0.0);
						paymentsCopy.add(j, payment);
					}

					diff = invoiceTemp - paymentsCopy.get(j).getPayment();
					if (diff < 0.0) {
						break;
					}
					if (invoiceCopy.get(i).getInvoiceTitle().equals(paymentsCopy.get(j).getPaymentTitle())) {
						Payment payment = paymentsCopy.get(j);
						paymentsCopy.remove(j);
						j = -1;

						if (diff == invoiceTemp) {
							Output output = setupOutput(invoiceCopy.get(i), payment, invoiceTemp);
							outputs.add(output);
							double invoiceCost = invoiceCopy.get(i).getInvoice();
							invoiceCopy.remove(i);
							invoiceCopy.add(new Invoice(date, invoiceCost));
							break;
						}

						Output output = setupOutput(invoiceCopy.get(i), payment, invoiceTemp);
						outputs.add(output);
						invoiceTemp = invoiceTemp - payment.getPayment();
					}
					if (paymentsCopy.size() - 1 == j && paymentsCopy.size() > 0 && invoiceCopy.size() > 0) {
						j = -1;
						//	break;
					}
				}
				if (diff != invoiceTemp && diff < 0.0) {
					invoiceCopy.remove(i);
				}
				if (invoiceCopy.size() - 1 == i && invoiceCopy.size() > 0 && paymentsCopy.size() > 0) {
					i = -1;
				}
			}
			return outputs;
		}
	}
}

