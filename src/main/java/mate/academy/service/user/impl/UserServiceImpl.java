package mate.academy.service.user.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.role.RoleRepository;
import mate.academy.repository.user.UserRepository;
import mate.academy.service.shoppingcart.ShoppingCartService;
import mate.academy.service.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RegistrationException("Unable to compare registration.");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        Role defaultRoleForNewUser = roleRepository.findByName(Role.RoleName.USER).orElseThrow(
                () -> new EntityNotFoundException("Can't find default role."));
        user.setRoles(Set.of(defaultRoleForNewUser));
        User savedUser = userRepository.save(user);
        shoppingCartService.createShoppingCart(savedUser);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
