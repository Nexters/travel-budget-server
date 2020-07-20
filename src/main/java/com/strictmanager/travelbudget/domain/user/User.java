package com.strictmanager.travelbudget.domain.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@EntityListeners({AuditingEntityListener.class})
@Getter
public class User implements UserDetails {

    @Builder
    private User(
        String kakaoId,
        String nickname,
        String profileImage,
        String thumbnailImage
    ) {
        this.kakaoId = Objects.requireNonNull(kakaoId);
        this.nickname = Objects.requireNonNull(nickname);
        this.profileImage = Objects.requireNonNull(profileImage);
        this.thumbnailImage = Objects.requireNonNull(thumbnailImage);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "kakao_id")
    private String kakaoId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "thumbnail_image")
    private String thumbnailImage;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_dt", updatable = false)
    private Date createDt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_dt")
    private Date updateDt;

    @Transient
    private String password;
    @Transient
    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return id.toString();
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
