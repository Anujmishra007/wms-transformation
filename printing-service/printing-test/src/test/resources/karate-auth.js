function fn() {
  // Mock authentication for testing
  // In production, this would obtain a real JWT token
  var token = 'Bearer test-jwt-token-for-printing-service';

  return {
    token: token
  };
}
