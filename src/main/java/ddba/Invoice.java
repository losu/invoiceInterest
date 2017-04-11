package ddba;

import java.time.LocalDate;

/**
 * Created by ddba on 31/03/2017.
 */
public class Invoice {
	private String invoiceTitle;
	private LocalDate deadlineDate;
	private double invoice;

	public Invoice(LocalDate deadlineDate, double invoice) {
		this.deadlineDate = deadlineDate;
		this.invoice = invoice;
		this.invoiceTitle = "";

	}

	public Invoice(String invoiceTitle, LocalDate deadlineDate, double invoice) {
		this.invoiceTitle = invoiceTitle;
		this.deadlineDate = deadlineDate;
		this.invoice = invoice;
	}

	public LocalDate getDeadlineDate() {
		return deadlineDate;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoice(double invoice) {
		this.invoice = invoice;
	}

	public void setDeadlineDate(LocalDate deadlineDate) {
		this.deadlineDate = deadlineDate;
	}

	public double getInvoice() {
		return invoice;
	}
}
