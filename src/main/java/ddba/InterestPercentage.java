package ddba;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by ddba on 30/03/2017.
 */
@Component
public class InterestPercentage {
	static List<StatutoryInterest> listOFInterest;

	public InterestPercentage() {
		this.listOFInterest = new LinkedList<>();
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1990, 1, 1), 720.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1990, 2, 1), 480.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1990, 3, 1), 216.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1990, 4, 1), 216.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1990, 5, 1), 144.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1990, 7, 1), 60.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1990, 12, 1), 90.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1991, 3, 1), 140.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1991, 9, 15), 80.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1992, 8, 15), 60.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1993, 5, 1), 54.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1995, 12, 15), 46.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1997, 1, 1), 35.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1998, 4, 15), 33.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1999, 2, 1), 24.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(1999, 5, 15), 21.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2000, 11, 1), 30.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2001, 12, 15), 20.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2002, 7, 25), 16.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2003, 2, 1), 13.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2003, 9, 25), 12.25));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2005, 1, 10), 13.50));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2005, 10, 15), 11.50));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2008, 12, 15), 13.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2014, 12, 23), 8.00));
		listOFInterest.add(new StatutoryInterest(LocalDate.of(2016, 1, 1), 5.00));
	}

	public List<StatutoryInterest> getListOFInterest() {
		return listOFInterest;
	}

	/**
	 * it checks the given date to decide what is the interest percentage int that time
	 *
	 * @param date
	 * @return - double value of interest percentage
	 * @info -
	 */
	public static double decideInterestPercentage(LocalDate date) {
		if (date.isAfter(listOFInterest.get(listOFInterest.size() - 1).getDate()))
			return listOFInterest.get(listOFInterest.size() - 1).getInterestPercentage();
		for (int i = 0; i < listOFInterest.size(); i++) {
			if ((date.isAfter(listOFInterest.get(i).getDate()) || date.isEqual(listOFInterest.get(i).getDate()))
					&& date.isBefore(listOFInterest.get(i + 1).getDate())) {
				return listOFInterest.get(i).getInterestPercentage();
			}
		}
		return 0;
	}

	public static class InterestCalculator {
		public static long daysOverDeadline(LocalDate deadlineDate, LocalDate paymentDate, boolean flag) {
			if (flag)
				return DAYS.between(deadlineDate, paymentDate);
			else
				return DAYS.between(deadlineDate, paymentDate);
		}

		/**
		 * it takes to parametes deadlineTime and actual payment date and it calculates
		 * the interest which has to be paid for that period of time
		 *
		 * @param invoice
		 * @param paymentDate
		 * @return
		 */
		public static double calculateInterest(LocalDate deadlineDate, double invoice, LocalDate paymentDate) {

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

		/**
		 * it calculates the interest which has to be paid as a fee for the period od time between deadline and
		 * be late actual date of payment.
		 * <p>
		 * Method takes two list of data. First one is the whole list of invoices which has to be paid. Second one
		 * is a queue of payments which has information of amount of money paid and when it was paid
		 *
		 * @param invoice
		 * @param payments
		 * @return
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
							Output output = setupOutput(invoice.getFirst(), payment, invoiceTemp, true);
							outputs.add(output);
							double invoiceCost = invoiceCopy.get(i).getInvoice();
							invoiceCopy.remove(i);
							invoiceCopy.add(new Invoice(date, invoiceCost));
							break;
						}

						Output output = setupOutput(invoiceCopy.get(i), payment, invoiceTemp, false);
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
		 * it calculates the interest which has to be paid as a fee for the period od time between deadline and
		 * be late actual date of payment.
		 * <p>
		 * Method takes two list of data. First one is the whole list of invoices which has to be paid. Second one
		 * is a queue of payments which has information of amount of money paid and when it was paid
		 *
		 * @param invoice
		 * @param payments
		 * @param now
		 * @return
		 */
		public static List<Output> calculateInteresTt(ArrayDeque<Invoice> invoice, ArrayDeque<Payment> payments, LocalDate now) {
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
							Output output = setupOutput(invoiceCopy.get(i), payment, invoiceTemp, true);
							outputs.add(output);
							double invoiceCost = invoiceCopy.get(i).getInvoice();
							invoiceCopy.remove(i);
							invoiceCopy.add(new Invoice(date, invoiceCost));
							break;
						}

						Output output = setupOutput(invoiceCopy.get(i), payment, invoiceTemp, false);
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

		public static Output setupOutput(Invoice invoice, Payment payment, double invoiceTemp, boolean flag) {
			Output output = new Output();
			output.setDaysOverDeadline(daysOverDeadline(invoice.getDeadlineDate(), payment.getPaymentDate(), flag));
			output.setPeriod(payment.getPaymentDate().minusDays(output.getDaysOverDeadline()) + " - " + payment.getPaymentDate());
			output.setInterestPercentage(decideInterestPercentage(invoice.getDeadlineDate()));
			output.setInterest(calculateInterest(invoice.getDeadlineDate(), invoiceTemp, payment.getPaymentDate()));

			return output;
		}
	}

	public static LocalDate decide(LocalDate start, LocalDate end) {
		List<LocalDate> dates = new ArrayList<>();
		for (StatutoryInterest statutoryInterest : listOFInterest) {
			if (statutoryInterest.getDate().isAfter(start) && statutoryInterest.getDate().isBefore(end)) {
				dates.add(statutoryInterest.getDate());
				return statutoryInterest.getDate();
			}
		}
		return null;
	}

//	public static List<LocalDate> decideList(LocalDate start, LocalDate end) {
//		List<LocalDate> dates = new ArrayList<>();
//		for (StatutoryInterest statutoryInterest : listOFInterest) {
//			if (statutoryInterest.getDate().isAfter(start) && statutoryInterest.getDate().isBefore(end)) {
//				dates.add(statutoryInterest.getDate());
//			}
//		}
//		if (dates.isEmpty())
//			return null;
//		else
//			return dates;
//	}
}

