package com.sky.backend.authorization.service;

import com.sky.backend.authorization.domain.dto.FetchUserDto;
import com.sky.backend.authorization.domain.dto.RegisterDto;
import com.sky.backend.authorization.domain.dto.UserDto;
import com.sky.backend.authorization.domain.entity.User;
import com.sky.backend.authorization.domain.mappers.IFetchMapper;
import com.sky.backend.authorization.domain.mappers.IUserMapper;
import com.sky.backend.authorization.exception.ForbiddenException;
import com.sky.backend.authorization.exception.UserAlreadyExistsException;
import com.sky.backend.authorization.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final IUserRepository repository;
    private final IFetchMapper fetchMapper;
    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        UserDetails userDetails = repository.findByLogin(username);
        if (userDetails == null){
            throw new ForbiddenException("FORBIDDEN");
        }
        return userDetails;
    }

    public UserDto register(final RegisterDto data) {

        final var user = repository.findByLogin(data.login());

        if( user != null ) {
            throw new UserAlreadyExistsException();
        }

        final var encryptedPassword = passwordEncoder.encode(data.password());
        final var newUser = new User(data.login(), encryptedPassword, data.role());
        return userMapper.toDto(repository.save(newUser));
    }

    public void deleteUserById(final String id) {
        repository.deleteById(id);
    }

    public Page<FetchUserDto> getAllUsers(final int page, final int size) {
        return repository.findAll(PageRequest.of(page, size)).map(fetchMapper::toDto);
    }

    public FetchUserDto findUserById(final String id) {
        return fetchMapper.toDto(repository.findUserById(id));
    }
}