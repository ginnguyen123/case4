package com.cg.model;

import com.cg.model.dto.TransferDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "transfers")
@Accessors(chain = true)
public class Transfer extends BaseEntity implements Validator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private Customer sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id", nullable = false)
    private Customer recipient;

    @Column(name = "transfer_amount", precision = 10, scale = 0, nullable = false)
    BigDecimal transferAmount;

    Long fees;

    @Column(name = "fees_amount", precision = 10, scale = 0, nullable = false)
    BigDecimal feesAmount;

    @Column(name = "transaction_amount", precision = 10, scale = 0, nullable = false)
    BigDecimal transactionAmount;

    public Transfer() {
    }

    public Transfer(Long id, Customer sender, Customer recipient, BigDecimal transferAmount, Long fees, BigDecimal feesAmount, BigDecimal transactionAmount) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.transferAmount = transferAmount;
        this.fees = fees;
        this.feesAmount = feesAmount;
        this.transactionAmount = transactionAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getSender() {
        return sender;
    }

    public void setSender(Customer sender) {
        this.sender = sender;
    }

    public Customer getRecipient() {
        return recipient;
    }

    public void setRecipient(Customer recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public Long getFees() {
        return fees;
    }

    public void setFees(Long fees) {
        this.fees = fees;
    }

    public BigDecimal getFeesAmount() {
        return feesAmount;
    }

    public void setFeesAmount(BigDecimal feesAmount) {
        this.feesAmount = feesAmount;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Transfer.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Transfer transfer = (Transfer) target;

        BigDecimal transferAmount = transfer.getTransferAmount();

        if (!transferAmount.toString().matches("^[0-9]{1,}$")){
            errors.rejectValue("transferAmount","transferAmount.matches");
        }
        if (transferAmount.toString().length() > 11){
            errors.rejectValue("transferAmount", "transferAmount.length");
        }

    }

    public TransferDTO toTransferDTO(){
        return new TransferDTO()
                .setRecipientId(String.valueOf(getRecipient().getId()))
                .setFees(String.valueOf(fees))
                .setSenderId(String.valueOf(sender.getId()))
                .setTransferAmount(String.valueOf(transferAmount))
                .setFeesAmount(String.valueOf(feesAmount))
                .setTransactionAmount(String.valueOf(transactionAmount));
    }

}
