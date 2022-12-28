package com.ar.uber.model;

import java.time.ZonedDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.format.annotation.DateTimeFormat;

import com.ar.uber.model.converter.RatingAttributeConverter;
import com.ar.uber.payment.model.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "t_trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Trip {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="latitude",
                    column=@Column(name="end_latitude")),
            @AttributeOverride(name="longitude",
                    column=@Column(name="end_longitude"))
    })
    private Location endLocation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="latitude",
                    column=@Column(name="start_latitude")),
            @AttributeOverride(name="longitude",
                    column=@Column(name="start_longitude"))
    })
    private Location startLocation;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @Convert(converter = RatingAttributeConverter.class)
    private DriverRating driverRating;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "fk_driver_id", referencedColumnName = "id")
    private Driver driver;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_payment_id", referencedColumnName = "id")
    private Payment payment;

    @Version
    private long version;

}
