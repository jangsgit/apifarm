package mes.domain.security;

import java.util.ArrayList;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import mes.domain.entity.User;
import mes.domain.entity.UserGroup;
import mes.domain.repository.UserRepository;

@Slf4j
@Component
public class CustomAuthenticationManager implements AuthenticationManager{

	@Autowired
	UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		// 사용자 조회
		Optional<User> optUser = this.userRepository.findByUsername(username);
		if (optUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found: " + username);
		}

		User user = optUser.get();
		String storedPassword = user.getPassword();

		// 비밀번호 검증
		if (!isPasswordValid(password, storedPassword, user)) {
			throw new AuthenticationException("Invalid credentials") {};
		}

		// 사용자 권한 추가
		UserGroup group = user.getUserProfile().getUserGroup();
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(group.getCode());
		ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(authority);

		return new CustomAuthenticationToken(user, password, authorities);
	}

	/**
	 * 비밀번호 검증 로직
	 * @param rawPassword 입력된 비밀번호
	 * @param storedPassword 저장된 비밀번호
	 * @param user 사용자 정보
	 * @return 검증 결과
	 */
	private boolean isPasswordValid(String rawPassword, String storedPassword, User user) {
		// 신규 방식 (BCrypt) 처리
		if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
			return passwordEncoder.matches(rawPassword, storedPassword);
		}

		// 기존 방식 (PBKDF2) 처리
		if (storedPassword.startsWith("pbkdf2_sha256$")) {
			boolean valid = Pbkdf2Sha256.verification(rawPassword, storedPassword);

			return valid;
		}

		// 알 수 없는 형식
		log.error("Unknown password format for user: {}", user.getUsername());
		throw new AuthenticationException("Unknown password format") {};
	}

}

