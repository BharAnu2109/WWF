package com.wwf.application.kafka;

import com.wwf.application.model.DonationStatus;

import java.math.BigDecimal;

/**
 * Event published when a donation is processed
 */
public class DonationProcessedEvent extends BaseEvent {
    
    private Long donationId;
    private Long projectId;
    private String projectName;
    private BigDecimal amount;
    private String donorName;
    private String donorEmail;
    private DonationStatus status;
    private String transactionId;

    public DonationProcessedEvent() {
        super("DONATION_PROCESSED");
    }

    public DonationProcessedEvent(Long donationId, Long projectId, BigDecimal amount, 
                                String donorName, DonationStatus status) {
        this();
        this.donationId = donationId;
        this.projectId = projectId;
        this.amount = amount;
        this.donorName = donorName;
        this.status = status;
    }

    // Getters and Setters
    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "DonationProcessedEvent{" +
                "donationId=" + donationId +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", amount=" + amount +
                ", donorName='" + donorName + '\'' +
                ", donorEmail='" + donorEmail + '\'' +
                ", status=" + status +
                ", transactionId='" + transactionId + '\'' +
                "} " + super.toString();
    }
}