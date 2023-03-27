package com.cg.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TransferDTO {

    @NotEmpty(message = "Recipient cannot be empty")
    private String recipientId;

    private String senderId;

    @NotEmpty(message = "Transfer Amount cannot be empty")
    private String transferAmount;

    @NotEmpty(message = "Fees cannot be empty")
    private String fees;

    private String feesAmount;

    private String transactionAmount;

}
