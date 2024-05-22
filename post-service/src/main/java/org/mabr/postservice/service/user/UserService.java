package org.mabr.postservice.service.user;

import org.mabr.postservice.dto.user.AuthUserDto;
import org.mabr.postservice.dto.user.UserProfile;
import org.mabr.postservice.entity.security.User;
import org.mabr.postservice.exception.ResourceAlreadyExistsException;
import org.mabr.postservice.exception.ResourceNotFoundException;
import org.mabr.postservice.repository.data.CityRepository;
import org.mabr.postservice.repository.data.CurrencyRepository;
import org.mabr.postservice.repository.user.UserRepository;
import org.mabr.postservice.service.data.DataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataService dataService;

    @Transactional
    public ResponseEntity<?> create(AuthUserDto authUserDto) {
        var dbUser = userRepository.findByUsername(authUserDto.getUsername());
        if (dbUser.isPresent()) {
            throw new ResourceAlreadyExistsException(
                    String.format("User %s already exist", authUserDto.getUsername()));
        }

        var user = new User();
        user.setUsername(authUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(authUserDto.getPassword()));
        user.setFirstName("");
        user.setLastName("");
        user.setAvatar(null);
        user.setAge(0);
        user.setCurrency(currencyRepository.findByName("Не указано"));
        user.setCity(cityRepository.findByNameRu("Не указано"));

        return ResponseEntity.ok(userRepository.save(user));
    }

    public User getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public User updateInfo(UserProfile userDto) {
        var user = getUserProfile(userDto.username());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setAge(userDto.age());

        var currency = dataService.getCurrency(userDto.currencyId());
        user.setCurrency(currency);

        var city = dataService.getCity(userDto.cityId());
        user.setCity(city);

        return userRepository.save(user);
    }

    public User updateAvatar(String username, String imageLink) {
        var user = getUserProfile(username);
        var oldAvatar = user.getAvatar();
        var image = dataService.getImage(imageLink);

        user.setAvatar(image);

        user = userRepository.save(user);

        dataService.deleteImage(oldAvatar.getId());

        return user;
    }
}
