package com.nikhil.app.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.nikhil.app.models.User;
import com.nikhil.app.payload.request.LoginRequest;
import com.nikhil.app.payload.request.SignupRequest;
import com.nikhil.app.payload.response.JwtResponse;
import com.nikhil.app.payload.response.MessageResponse;
import com.nikhil.app.repository.UserRepository;
import com.nikhil.app.security.jwt.JwtUtils;
import com.nikhil.app.security.services.SequenceGeneratorService;
import com.nikhil.app.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/api/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(),
												 userDetails.getTicket(),
												 userDetails.getScore()));
	}

	@PostMapping("/api/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		Date date=new Date();
		long time=date.getTime();
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()),time);
		user.setId(SequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));

		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@PutMapping("/update/ticketandscore")
	public ResponseEntity<?> updateTicketAndScore(@RequestBody User userf){
		User user=userRepository.findById(userf.getId())
				.orElseThrow(() -> new UsernameNotFoundException("User id Not Found with id: " + userf.getId()));
		user.setTicket(userf.getTicket());
		user.setScore(userf.getScore());
		userRepository.save(user);
//		ScoreAndTicket scoreAndTicket = new ScoreAndTicket(userf.getTicket(),userf.getScore());
		user.setPassword("");
		return ResponseEntity.ok(user);
	}
	
	@PutMapping("/update/time")
	public ResponseEntity<?> updateTime(@RequestBody User userf){
		User user=userRepository.findById(userf.getId())
				.orElseThrow(() -> new UsernameNotFoundException("User id Not Found with id: " + userf.getId()));
		user.setTime(userf.getTime());
		userRepository.save(user);
		user.setPassword("");
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/get/topscore")
		public ResponseEntity<List<User>> getTopUserByScore(){
		List<User> list=userRepository.findAll(new Sort(Sort.Direction.DESC, "score"));
		List<User> firstNElementsList = list.stream().limit(10).collect(Collectors.toList());
			return ResponseEntity.ok(firstNElementsList);
		}
	
	@GetMapping("/get/user/{id}")
	public ResponseEntity<?> getUser(@PathVariable long id){
		User user=userRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User id Not Found with id: " + id));
		user.setPassword("");
		return ResponseEntity.ok(user);
	}
	
//	@GetMapping("/api/get")
//	public ResponseEntity<?> getTime() throws ParseException
//	{
//		Date date=new Date();
//		Date date1=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2032-06-26 16:00:00");
//		long l1=date1.getTime()/1000;
//		long l=date.getTime()/1000;
//		return ResponseEntity.ok(l);
//	}
}
