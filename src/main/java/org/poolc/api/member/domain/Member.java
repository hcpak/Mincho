package org.poolc.api.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import org.poolc.api.auth.exception.UnauthorizedException;
import org.poolc.api.common.domain.TimestampEntity;
import org.poolc.api.member.dto.UpdateMemberRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity(name = "Member")
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member extends TimestampEntity implements UserDetails {
    @Id
    @Column(name = "uuid", length = 40)
    private String UUID;

    @Column(name = "login_id", unique = true, columnDefinition = "varchar(40)", nullable = false)
    private String loginID;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", unique = true, nullable = false, columnDefinition = "varchar(255)")
    private String email;

    @Column(name = "phone_number", nullable = false, columnDefinition = "varchar(20)")
    private String phoneNumber;

    @Column(name = "name", nullable = false, columnDefinition = "varchar(40)")
    private String name;

    @Column(name = "department", nullable = false, columnDefinition = "varchar(40)")
    private String department;

    @Column(name = "student_id", unique = true, nullable = false, columnDefinition = "varchar(40)")
    private String studentID;

    @Column(name = "password_reset_token", columnDefinition = "varchar(255)")
    private String passwordResetToken;

    @Column(name = "password_reset_token_valid_until")
    private LocalDateTime passwordResetTokenValidUntil;

    @Column(name = "profile_image_url", columnDefinition = "varchar(255)")
    private String profileImageURL;

    @Column(name = "introduction", columnDefinition = "varchar(1024)")
    private String introduction;

    @Column(name = "is_excepted", columnDefinition = "boolean default false")
    private Boolean isExcepted;

    @Embedded
    private MemberRoles roles;

    protected Member() {
    }

    public Member(String UUID, String loginID, String passwordHash, String email, String phoneNumber, String name, String department, String studentID, String passwordResetToken, LocalDateTime passwordResetTokenValidUntil, String profileImageURL, String introduction, Boolean isExcepted, MemberRoles roles) {
        this.UUID = UUID;
        this.loginID = loginID;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.department = department;
        this.studentID = studentID;
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenValidUntil = passwordResetTokenValidUntil;
        this.profileImageURL = profileImageURL;
        this.introduction = introduction;
        this.isExcepted = isExcepted;
        this.roles = roles;
    }

    public void updateMemberInfo(UpdateMemberRequest updateMemberRequest, String passwordHash) {
        this.name = updateMemberRequest.getName();
        this.passwordHash = passwordHash;
        this.email = updateMemberRequest.getEmail();
        this.phoneNumber = updateMemberRequest.getPhoneNumber();
        this.introduction = updateMemberRequest.getIntroduction();
        this.profileImageURL = updateMemberRequest.getProfileImageURL();
    }

    public boolean isAcceptedMember() {
        return isEnabled();
    }

    public boolean isMember() {
        return roles.isMember();
    }

    public boolean isAdmin() {
        return roles.isAdmin();
    }

    public String getRole() {
        return roles.getHighestRole().name();
    }

    public boolean shouldHide() {
        return roles.getHighestRole().isHideInfo();
    }

    public void acceptMember() {
        roles = MemberRoles.getDefaultFor(MemberRole.MEMBER);
    }

    public void quit() {
        roles = MemberRoles.getDefaultFor(MemberRole.QUIT);
    }

    public void changeRole(Member targetMember, MemberRole role) {
        onlyAdmin();

        if (targetMember.getRole().equals(MemberRole.SUPER_ADMIN.name())) {
            throw new UnauthorizedException("Usage of super admin is prohibited");
        }

        targetMember.roles.changeRole(role);
    }

    public void selfChangeRole(MemberRole role) {
        checkHasCorrectPermissions(role);

        if (getRole().equals(MemberRole.SUPER_ADMIN.name())) {
            throw new UnauthorizedException("Usage of super admin is prohibited");
        }

        roles.changeRole(role);
    }

    public void toggleExcept(Member targetMember) {
        onlyAdmin();
        targetMember.toggleIsExcepted();
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenValidUntil = LocalDateTime.now().plusDays(1l);
    }

    public void updatePassword(String newPasswordHash) {
        if (!this.passwordResetTokenValidUntil.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        this.passwordResetTokenValidUntil = null;
        this.passwordResetToken = null;
        this.passwordHash = newPasswordHash;
    }

    private void onlyAdmin() {
        if (!isAdmin()) {
            throw new UnauthorizedException("Only admins can do this");
        }
    }

    private void checkHasCorrectPermissions(MemberRole role) {
        if (!role.isSelfToggleable()) {
            throw new UnauthorizedException(String.format("Role %s cannot be self toggled", role.name()));
        }
    }

    private void toggleIsExcepted() {
        isExcepted = !isExcepted;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.getAuthorities();
    }

    @Override
    public String getUsername() {
        return loginID;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isEnabled() {
        return roles.isAcceptedMember();
    }

    @Override
    public boolean isAccountNonExpired() {
        return roles.isExpelled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
