package com.ar.uber.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ar.uber.appuser.AppUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "t_drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id") //refactor equals and hashcode using @AppUser example.
@ToString(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Driver implements Serializable {

    @Id
    private Long id;

    @MapsId
    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private AppUser appUser;

    @Audited
    @OneToOne
    @JoinColumn(name = "fk_vehicle_id", referencedColumnName = "id")
    private Vehicle vehicle;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();
}
