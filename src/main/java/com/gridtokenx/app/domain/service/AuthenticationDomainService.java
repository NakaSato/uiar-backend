package com.gridtokenx.app.domain.service;

import com.gridtokenx.app.domain.entity.User;

/**
 * Domain service for authentication business logic
 * Contains domain rules that don't naturally fit within a single entity
 */
public interface AuthenticationDomainService {

  /**
   * Domain rule: Validate user credentials
   * 
   * @param user        the user entity
   * @param rawPassword the plain text password
   * @return true if credentials are valid
   */
  boolean validateCredentials(User user, String rawPassword);

  /**
   * Domain rule: Check if user can login
   * Combines account status checks with business rules
   * 
   * @param user the user entity
   * @return true if user is allowed to login
   */
  boolean canUserLogin(User user);

  /**
   * Domain rule: Update password with validation
   * 
   * @param user           the user entity
   * @param newRawPassword the new plain text password
   * @return true if password was successfully updated
   */
  boolean updatePassword(User user, String newRawPassword);

  /**
   * Domain rule: Determine if account should be locked
   * 
   * @param user the user entity
   * @return true if account should be locked
   */
  boolean shouldLockAccount(User user);
}
