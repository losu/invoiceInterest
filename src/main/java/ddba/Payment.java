package ddba;

import java.time.LocalDate;

public class Payment {

	private String paymentTitle;
	private LocalDate paymentDate;
	private double amount;

	public Payment(LocalDate paymentDate, double amount) {
		this.paymentDate = paymentDate;
		this.amount = amount;
		this.paymentTitle = "";
	}

	public Payment(String paymentTitle, LocalDate paymentDate, double amount) {
		this.paymentTitle = paymentTitle;
		this.paymentDate = paymentDate;
		this.amount = amount;
	}

	public String getPaymentTitle() {
		return paymentTitle;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public double getAmount() {
		return amount;
	}

}
