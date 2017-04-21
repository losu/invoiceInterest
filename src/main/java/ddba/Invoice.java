package ddba;

import java.time.LocalDate;

public class Invoice {
	private String invoiceTitle;
	private LocalDate deadlineDate;
	private double amount;

	public Invoice(LocalDate deadlineDate, double amount) {
		this.deadlineDate = deadlineDate;
		this.amount = amount;
		this.invoiceTitle = "";
	}

	public Invoice(String invoiceTitle, LocalDate deadlineDate, double amount) {
		this.invoiceTitle = invoiceTitle;
		this.deadlineDate = deadlineDate;
		this.amount = amount;
	}

	public LocalDate getDeadlineDate() {
		return deadlineDate;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public double getAmount() {
		return amount;
	}
}
