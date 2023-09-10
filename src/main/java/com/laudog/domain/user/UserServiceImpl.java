package com.laudog.domain.user;

import com.laudog.domain.error.AppException;
import com.laudog.domain.error.Error;
import com.laudog.security.AuthUserDetails;
import com.laudog.security.BiscuitTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.laudog.security.BiscuitUtils.createFact;
import static reactor.core.publisher.Mono.error;

@Service
@RequiredArgsConstructor
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final BiscuitTokenProvider biscuitTokenProvider;

    @Override
    public Mono<UserDto> registration(final UserDto.Registration registration) {
        return userRepository.findByUsernameOrEmail(registration.getUsername(), registration.getEmail())
            .hasElements().map(has -> { if (has) throw new AppException(Error.DUPLICATED_USER); else return Mono.empty();})
            .flatMap(q -> userRepository.save(UserDocument.builder()
                .username(registration.getUsername())
                .email(registration.getEmail())
                .roles(registration.getRoles())
                .password(passwordEncoder.encode(registration.getPassword()))
                .build())).map(this::convertEntityToDto);
    }

    @Override
    public Mono<UserDto> login(UserDto.Login login) {
        return userRepository.findByEmail(login.getEmail())
            .filter(user -> passwordEncoder.matches(login.getPassword(), user.getPassword()))
            .switchIfEmpty(error(new AppException(Error.USER_NOT_FOUND)))
            .map(this::convertEntityToDto);
    }

    @Override
    public Mono<UserDto> currentUser(AuthUserDetails authUserDetails) {
        return userRepository.findByEmail(authUserDetails.getEmail())
            .switchIfEmpty(error(new AppException(Error.USER_NOT_FOUND)))
            .map(this::convertEntityToDto);
    }

    @Override
    public Mono<UserDto> update(UserDto.Update update, AuthUserDetails authUserDetails) {
        return userRepository.findByEmail(authUserDetails.getEmail())
        .switchIfEmpty(error(new AppException(Error.USER_NOT_FOUND)))
        .flatMap(ud -> {
            Mono<Boolean> duplicatedUserName;
            Mono<Boolean> duplicatedEmail;
            if (update.getUsername() != null) {
                duplicatedUserName = userRepository.findByUsername(update.getUsername())
                    .filter(found -> !found.getId().equals(ud.getId()))
                    .hasElement();
            } else {
                duplicatedUserName = Mono.just(false);
            }
            if (update.getEmail() != null) {
                duplicatedEmail = userRepository.findByUsername(update.getUsername())
                    .filter(found -> !found.getId().equals(ud.getId()))
                    .hasElement();
            } else {
                duplicatedEmail = Mono.just(false);
            }

            return duplicatedUserName.zipWith(duplicatedEmail)
                .flatMap( tuple -> {
                    if (tuple.getT1() || tuple.getT2())
                        return error(new AppException(Error.DUPLICATED_USER));
                    else {
                        if (update.getUsername() != null) {
                            ud.setUsername(update.getUsername());
                        }
                        if (update.getEmail() != null) {
                            ud.setEmail(update.getEmail());
                        }
                        if (update.getBio() != null) {
                            ud.setBio(update.getBio());
                        }
                        if (update.getImage() != null) {
                            ud.setImage(update.getImage());
                        }
                        if (update.getRoles() != null) {
                            ud.setRoles(update.getRoles());
                        }
                        return this.userRepository.save(ud).map(this::convertEntityToDto);
                    }
                });
        });
    }

    public Flux<UserDto> findAllUsers() {
        return userRepository.findAll()
            .map(this::convertEntityToDto);
    }

    public Mono<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(this::convertEntityToDto);
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }

    private UserDto convertEntityToDto(UserDocument userEntity) {
        return UserDto.builder()
            .id(userEntity.getId())
            .password(userEntity.getPassword())
            .username(userEntity.getUsername())
            .bio(userEntity.getBio())
            .email(userEntity.getEmail())
            .image(userEntity.getImage())
            .roles(userEntity.getRoles())
            .token(biscuitTokenProvider.createToken(
                userEntity.getEmail(), userEntity.getRoles().stream().map(
                    role -> createFact("role", role)).toList())
            )
            .build();
    }


}

