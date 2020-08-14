package com.example.sweater.domain;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usr")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    private String username;
    @NotBlank(message = "Password cannot be empty")
    private String password;
//    этот тег означает, что не нужно пытаться получить это поле из БД или сохранить его в БД
//    @Transient
//    @NotBlank(message = "Password confirmation cannot be empty")
//    private String password2;
    private boolean active;

    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

//    lazy - при загрузке прльзователя не будут подгружаться все его сообщения
//    mappedBy - нужно указать для обратной связи, т.к. у нас не user, а author
    @OneToMany(mappedBy ="author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages;

    //    мэппинг ManyToMany между пользователем и полбьзователем (для простых подписок в приложении)
//    для обеспечения связи между таблицами нам потребуется дополнительная таблица
//    для этого используем спец аннотацию JoinTable
//    name = "user_subscriptions"  - название новой таблицы,
//    joinColumns = {@JoinColumn(name = "channel_id")} - это коллекция, котрая будет состоять из одного элемента (будем исходить
//    из того, что пользователь, на которого подписываемся - это канал),
//    private Set<User> subscribers  (этот сет - это список подписчиков,
//    чтобы не возникало NullPointerException (NPE) мы сразу здесь добавим реализацию?, сета)= new HashSet<>();
    // В ЭТОМ ПОЛЕ У НАС БУДУТ ХРАНИТЬСЯ ПОДПИСЧИКИ (те, кто на меня подписаны)
    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = {@JoinColumn(name = "channel_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscriber_id")}
    )
    private Set<User> subscribers = new HashSet<>();

    // В ЭТОМ ПОЛЕ У НАС БУДУТ ХРАНИТЬСЯ наши собственные подписки (те, на кого я подписан)
    //в одной таблице у нас будут храниться и подписки и подписчики
    //раз мдобавили подписчиков - нужна миграция (4)
    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = {@JoinColumn(name = "subscriber_id")},
            inverseJoinColumns = {@JoinColumn(name = "channel_id")}
    )
    private Set<User> subscriptions = new HashSet<>();

//    переопределили equals для сравнениея currentUser.equals(user) в Main.Controller
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
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
        return isActive();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<User> subscribers) {
        this.subscribers = subscribers;
    }

    public Set<User> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<User> subscriptions) {
        this.subscriptions = subscriptions;
    }

}
