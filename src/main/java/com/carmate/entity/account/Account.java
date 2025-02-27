package com.carmate.entity.account;

import com.carmate.entity.car.Car;
import com.carmate.entity.notification.Notification;
import com.carmate.enums.AccountRoleEnum;
import com.carmate.enums.LanguageEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private AccountRoleEnum role;

    @Enumerated(EnumType.STRING)
    private LanguageEnum language = LanguageEnum.BULGARIAN;

    private String token;

    private String fcmToken;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Car> cars;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
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
}
