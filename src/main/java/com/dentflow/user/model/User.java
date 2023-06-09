package com.dentflow.user.model;

import com.dentflow.clinic.model.Clinic;
import com.dentflow.hoursOfAvailability.model.HoursOfAvailability;
import com.dentflow.patient.model.Patient;
import com.dentflow.visit.model.Visit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String phoneNumber;
    private String pesel;
    private LocalDate birthDate;
    private Set<Role> roles ;

    @OneToMany(mappedBy = "user")
    @Column(name = "user_hours")
    private Set<HoursOfAvailability> hoursOfAvailability;

    @OneToMany(mappedBy = "myUserAccount")
    @Column(name = "user_accounts")
    private Set<Patient> myPatientsAccounts;

    @OneToOne(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Clinic ownedClinic;

    @ManyToMany(mappedBy = "personnel", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Clinic> clinics;

    public void clearClinics(){
        for (Clinic clinic : clinics) {
            clinic.removeEmployee(this);
        }
        this.clinics.clear();
    }

    public void addRole(Role role){
        roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addMyPatientsAccount(Patient myPatientAccount) {
        this.myPatientsAccounts.add(myPatientAccount);
    }

}
