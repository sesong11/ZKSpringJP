package com.sample.zkspring.entity.employment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sample.zkspring.utils.Frequency;
import com.sample.zkspring.utils.Unit;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "allowance", schema = "employment")
public class Allowance {
    //region > Fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @Getter @Setter
    private String name;

    @Column(name = "type")
    @Getter @Setter
    private AllowanceType allowanceType;

    @Column(name = "unit")
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private Unit unit;

    @Column(name = "end_balance")
    @Getter @Setter
    private double endBalance;

    @Column(name = "frequency_accrual")
    @Getter @Setter
    private Frequency frequencyAccrual;

    @Column(name = "no_frequency_accrual")
    @Getter @Setter
    private int noFrequencyAccrual;

    @Column(name = "frequency_renew")
    @Getter @Setter
    private Frequency frequencyRenew;

    @Column(name = "no_frequency_renew")
    @Getter @Setter
    private int noFrequencyRenew;

    @Column(name = "accrual_balance")
    @Getter @Setter
    private double accrualBalance;

    @Column(name = "start_balance")
    @Getter @Setter
    private double startBalance;

    @Column(name = "max_renewal")
    @Setter @Getter
    private double maxRenewal;

    @Column(name = "accrual_on")
    @Getter @Setter
    private double accrualOn;
    //endregion
}
