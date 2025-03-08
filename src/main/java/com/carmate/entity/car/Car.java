package com.carmate.entity.car;

import com.carmate.entity.account.Account;
import com.carmate.entity.expense.Expense;
import com.carmate.entity.insurance.Insurance;
import com.carmate.entity.obligation.Obligation;
import com.carmate.entity.technicalReview.TechnicalReview;
import com.carmate.entity.tripSheet.TripSheet;
import com.carmate.entity.vignette.Vignette;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String plateNumber;

    @Column
    private String egn;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripSheet> tripSheets;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vignette_id", referencedColumnName = "id")
    private Vignette vignette;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "technical_review_id", referencedColumnName = "id")
    private TechnicalReview technicalReview;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "insurance_id", referencedColumnName = "id")
    private Insurance insurance;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "obligation_id", referencedColumnName = "id")
    private Obligation obligation;

    @Column
    private Long oilChangeOdometer;

    @Column
    private Date oilChangeDate;
}
