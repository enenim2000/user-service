package com.elara.userservice.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "NotificationCache")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCache {

    @Column(name = "token", unique = true)
    private String token;//SHA 256 Hash of companyCode,userId,notificationType,otp

    @Column(name = "companyCode")
    private String companyCode;

    @Column(name = "userId")
    private long userId;

    @Column(name = "notificationType")
    private String notificationType;

    @Column(name = "otp")
    private String otp;

    @Column(name = "expiry")
    private Date expiry;

}
