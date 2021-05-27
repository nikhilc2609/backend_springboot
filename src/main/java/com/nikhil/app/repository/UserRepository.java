package com.nikhil.app.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nikhil.app.models.User;
@Repository
public interface UserRepository extends  MongoRepository<User, Long>{
	Optional<User> findByUsername(String username);
	
	Optional<User> findById(Long id);
	
	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}
