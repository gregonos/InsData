package net.windia.insdata.model.internal;

import java.time.DayOfWeek;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "user")
public class InsDataUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String timeZone;

    @Column(nullable = false)
    private Byte firstDayOfWeek;

    @Column
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Transient
    public ZoneId getZoneId() {
        return ZoneId.of(getTimeZone());
    }

    public Byte getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    @Transient
    public DayOfWeek getFirstDayOfWeekInstance() {
        return DayOfWeek.of(firstDayOfWeek);
    }

    public void setFirstDayOfWeek(Byte firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
