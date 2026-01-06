package com.quickpos.quickposbackend.model;

import com.quickpos.quickposbackend.model.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import com.quickpos.quickposbackend.model.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Transaction {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;
    private double totalAmount;

    @ManyToOne
    private User Employee;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<Order> orders;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}
