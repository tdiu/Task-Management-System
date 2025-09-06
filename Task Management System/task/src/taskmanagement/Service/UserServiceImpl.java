package taskmanagement.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import taskmanagement.DAO.AppUserRepository;
import taskmanagement.DAO.CommentRepository;
import taskmanagement.DAO.TaskRepository;
import taskmanagement.DTO.*;
import taskmanagement.Exception.*;
import taskmanagement.Model.AppUser;
import taskmanagement.Security.AppUserService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Autowired
    public UserServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
                           JwtEncoder jwtEncoder, AppUserService appUserService) {
        this.appUserRepository = appUserRepository;
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public UserDTO registerUser(RegistrationRequest request) {
        if (appUserRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateEmailException("Email already exists");
        }
        AppUser user = new AppUser(request);
        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPassword(encodedPassword);
        appUserRepository.save(user);

        return new UserDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        AppUser user = appUserRepository.findAppUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new UserDTO(user);
    }

    @Override
    public String generateToken(User user) {
        AppUser appUser = appUserRepository.findAppUserByUsernameIgnoreCase(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + user.getUsername()));

        Set<String> authorityStrings = appUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        JwtClaimsSet claimSet = JwtClaimsSet.builder()
                .subject(appUser.getUsername())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(60, ChronoUnit.SECONDS))
                .claim("scope", authorityStrings)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimSet)).getTokenValue();
    }

    @Override
    public User getAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user;

        if (auth.getPrincipal() instanceof User) {
            user = (User) auth.getPrincipal();
        } else {
            String username = (String) auth.getName();
            user = (User) appUserService.loadUserByUsername(username);
        }
        return user;
    }

    @Override
    public void validateBindingRes(BindingResult result) throws InvalidInformationException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            throw new InvalidInformationException("Validation error", errors);
        }
    }

}
